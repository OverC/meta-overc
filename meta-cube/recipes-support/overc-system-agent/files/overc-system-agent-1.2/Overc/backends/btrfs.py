import sys, os
import tempfile
import subprocess
from Overc.utils  import Utils
from Overc.utils  import ROOTMOUNT
from Overc.utils  import CONTAINER_MOUNT
from Overc.utils  import FACTORY_SNAPSHOT
from Overc.utils  import SYSROOT

class Btrfs(Utils):
    def __init__(self):
        self.name = None
        self.rootfs = None
        self.message = ""
        self.rootdev = self._getrootdev(ROOTMOUNT)
        self.bakup_mode = self._get_bootmode()
        self.rootfs_dic = {'rootfs':'rootfs_upgrade', 'rootfs_upgrade':'rootfs'}
                
        if os.path.exists('/boot/bzImage'):
            self.kernel = "/boot/bzImage"
        elif os.path.exists('/boot/uImage'):
            self.kernel = "/boot/uImage"
        elif os.path.exists('/boot/zImage'):
            self.kernel = "/boot/zImage"
        elif os.path.exists('/boot/fitImage'):
            self.kernel = "/boot/fitImage"
        elif os.path.exists('/boot/kernel7.img'):
            self.kernel = "/boot/kernel7.img"

        self.kernel_md5 = self._compute_checksum(self.kernel)

        if not self.rootdev or not self.rootfs:
            print "Error: cannot get the rootfs device!"
            sys.exit(2)

        self.next_rootfs = self.rootfs_dic[self.rootfs]

    def _btrfs(self, args):
        return os.system('/usr/bin/btrfs %s' % args)

    def _getrootdev(self, mountdir):
        subp=subprocess.Popen('mount',shell=True,stdout=subprocess.PIPE)
        c=subp.stdout.readline().strip()
        while c:
            c_list = c.split()
            if  c_list[2] == mountdir:
                return c_list[0]
            c=subp.stdout.readline()

        return None
    
    def _mount_rootvolume(self):
        if not os.path.exists(SYSROOT):
            os.mkdir(SYSROOT)

        subp=subprocess.Popen('mount',shell=True,stdout=subprocess.PIPE)
        c=subp.stdout.readline().strip()
        need_mount = True
        while c:
            c_list = c.split()
            if c_list[0] == self.rootdev and c_list[2] == SYSROOT:
                #root volume has been mount to SYSROOT
                need_mount = False
                break
            c=subp.stdout.readline()
            
        if need_mount:
            os.system('mount -o subvolid=5 %s %s' % (self.rootdev, SYSROOT))

    def _get_bootmode(self):
        self.rootfs = self._get_btrfs_value(ROOTMOUNT, "Name")
        if self.rootfs == "rootfs_bakup":
            return True
        else:
            return False

    def _get_btrfs_value(self, path, key):
        subp=subprocess.Popen('/usr/bin/btrfs subvolume show %s' % path, shell=True,stdout=subprocess.PIPE)
        c=subp.stdout.readline()
        while c:
            c_list = c.split(":", 1)
            if  c_list[0].strip() == key:
                return c_list[1].strip()
                
            c=subp.stdout.readline()

    def _cleanup_subvol(self, subvoldir, exclude):
        subp=subprocess.Popen('/usr/bin/btrfs subvolume list %s' % subvoldir,shell=True,stdout=subprocess.PIPE)
        c=subp.stdout.readline()
        subvol_stack = []
        while c:
            c_list = c.split()
            subvolid = c_list[-1].split('/')[0]
            if  subvolid != self.rootfs and subvolid not in exclude:
                subvol = 'subvolume delete -C %s/%s' % (subvoldir, c_list[-1])
                subvol_stack.append(subvol)
            c=subp.stdout.readline()

        while subvol_stack:
            self._btrfs(subvol_stack.pop())

    def clean_essential(self):
        self._mount_rootvolume()
        self._cleanup_subvol(SYSROOT, [FACTORY_SNAPSHOT, 'rootfs_bakup'])

    def clean_container(self):
        self._mount_container_root()
        current_subvol = self._get_btrfs_value(CONTAINER_MOUNT, 'Name')
        self._cleanup_subvol('%s/.tmp' % CONTAINER_MOUNT, [current_subvol, FACTORY_SNAPSHOT])
        os.system('umount %s/.tmp' % CONTAINER_MOUNT)
        
    def factory_reset(self):
        return self._factory_reset_essential() and self._factory_reset_container() 

    def _factory_reset_essential(self):
        self._mount_rootvolume()
        #first remove the snapshot of unused backup rootfs
        self._cleanup_subvol(SYSROOT, [FACTORY_SNAPSHOT])

        #check is the subvolumes are cleaned up
        if os.path.exists('%s/%s' % (SYSROOT, self.next_rootfs)):
            self.message = 'Cannot cleanup the subvolumes in root tree %s. There must be something wrong!' % SYSROOT
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            return False

        ret = self._btrfs('subvolume snapshot %s/%s %s/%s' % (SYSROOT, FACTORY_SNAPSHOT, SYSROOT, self.next_rootfs))
        if ret != 0:
            self.message += "Cannot factory reset, please check is there enough diskspace left"
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            return False

        #create snapshot for backup boot entry
        self._btrfs('subvolume snapshot %s/%s %s/rootfs_bakup' % (SYSROOT, FACTORY_SNAPSHOT, SYSROOT))

        #setup default subvolume
        subvolid = self._get_btrfs_value('%s/%s' % (SYSROOT, self.next_rootfs), 'Subvolume ID')
        argv = 'subvolume set-default %s %s' % (subvolid, SYSROOT)
        if not self._btrfs(argv):
            #factory-reset the kernel image
            self.message += "factory reset kernel %s \n" % self.kernel
            real_kernel = '%s/%s/%s' % (SYSROOT, FACTORY_SNAPSHOT, self.kernel)
            if os.path.islink(real_kernel):
                real_kernel = '%s/%s/%s' % (SYSROOT, FACTORY_SNAPSHOT, os.path.realpath(real_kernel))
            os.system('cp -rf %s %s' % (real_kernel, self.kernel))

            #if grub-efi exists, factory-reset those files from the original rootfs too.
            if os.path.exists('%s/%s/boot/efi/EFI/BOOT' % (SYSROOT, FACTORY_SNAPSHOT)):
                self.message += "factory reset grub-efi related files \n"
                #Here we only reback the efi binary files, excluding the grub.cfg and startup.nsh
                os.system('cp -rf %s/%s/boot/efi/EFI/BOOT/*efi /boot/EFI/BOOT/' % (SYSROOT, FACTORY_SNAPSHOT))
            return True
        else:
            self.message += "Cannot set default mount subvolume to %s" % self.next_rootfs
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            return False
   
    def _mount_container_root(self):
        if not os.path.exists('%s/.tmp' % CONTAINER_MOUNT):
            os.mkdir('%s/.tmp' % CONTAINER_MOUNT)
        devpath = self._getrootdev(CONTAINER_MOUNT)
        os.system('mount -o subvolid=5 %s %s/.tmp' % (devpath, CONTAINER_MOUNT))

    def _factory_reset_container(self):
        self._mount_container_root()
        if not os.path.exists('%s/.tmp/%s' % (CONTAINER_MOUNT, FACTORY_SNAPSHOT)):
            self.message += 'Error: Cannot find the snapshot of factory in %s/.tmp' % CONTAINER_MOUNT
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            os.system('umount %s/.tmp' % CONTAINER_MOUNT)
            return False
        work_subvol =  self._random_str()   
        workdir = '%s/.tmp/%s' % (CONTAINER_MOUNT, work_subvol)
        ret = self._btrfs('subvolume snapshot %s/.tmp/%s %s' % (CONTAINER_MOUNT, FACTORY_SNAPSHOT, workdir))
        if ret != 0:
            self.message += "Cannot factory reset, please check is there enough diskspace left"
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            os.system('umount %s/.tmp' % CONTAINER_MOUNT)
            return False

        #snapshot the children subvolumes
        subp=subprocess.Popen('/usr/bin/btrfs subvolume list -o %s/.tmp/%s' % (CONTAINER_MOUNT, FACTORY_SNAPSHOT),shell=True,stdout=subprocess.PIPE)
        c=subp.stdout.readline()
        while c:
            c_list = c.split()
            factory_subvol = c_list[-1]
            workdir_subvol = factory_subvol.replace(FACTORY_SNAPSHOT, '')
            snapshot_dir = '%s/%s' % (workdir, workdir_subvol)
            if os.path.exists(snapshot_dir):
                os.rmdir(snapshot_dir)
            ret = self._btrfs('subvolume snapshot %s/.tmp/%s %s' % (CONTAINER_MOUNT, factory_subvol, snapshot_dir))
            if ret != 0:
                self.message += 'Cannot snapshot subvolume of %s.tmp/%s' % (CONTAINER_MOUNT, factory_subvol)
    	        self.message += '\nto %s' % snapshot_dir
    	        self.message += 'Factory reset aborted!'
    	        return False 
            
            c=subp.stdout.readline()
        
        #setup default subvolume
        subvolid = self._get_btrfs_value(workdir, 'Subvolume ID')
        argv = 'subvolume set-default %s %s' % (subvolid, CONTAINER_MOUNT)
        if not self._btrfs(argv):
            os.system('umount %s/.tmp' % CONTAINER_MOUNT)
            return True
        else:
            self.message += "Cannot set default mount subvolume to %s" % workdir
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            os.system('umount %s/.tmp' % CONTAINER_MOUNT)
            return False
   
    def _do_upgrade(self):
        #first remove the previous rootfs and bakup current running rootfs
        self._cleanup_subvol(SYSROOT, [FACTORY_SNAPSHOT])
        bzImage = {'normal':'bzImage_bakup', 'bakup':'bzImage'}

        if os.path.exists('%s/%s' % (SYSROOT, self.next_rootfs)):
            self.message = 'Cannot cleanup the subvolumes in root tree %s. There must be something wrong!' % SYSROOT
            self.message += '\n'
            self.message += 'Upgrade aborted!'
            return False
            # sys.exit(2)

        if not self.bakup_mode:
            argv = 'subvolume snapshot %s/%s %s/rootfs_bakup' % (SYSROOT, self.rootfs, SYSROOT)
            self._btrfs(argv)

            #setup upgrade rootfs
            argv = 'subvolume snapshot %s/%s %s/%s' % (SYSROOT, self.rootfs, SYSROOT, self.next_rootfs)
            self._btrfs(argv)

            #do upgrade
            os.system('mount -t proc proc %s/%s/proc' % (SYSROOT, self.next_rootfs))
            os.system('mount -o bind /dev %s/%s/dev' % (SYSROOT, self.next_rootfs))
            os.system('mount --bind /run %s/%s/run' % (SYSROOT, self.next_rootfs))

            tempd = tempfile.mkdtemp(dir='%s/%s/tmp' % (SYSROOT, self.next_rootfs))
            os.system('cp -r %s/%s/var/lib/rpm/* %s' % (SYSROOT, self.next_rootfs, tempd))
            os.system('mount -t tmpfs tmpfs %s/%s/var/lib/rpm' % (SYSROOT, self.next_rootfs))
            os.system('cp -r %s/* %s/%s/var/lib/rpm/' % (tempd, SYSROOT, self.next_rootfs))

            result = os.system('chroot %s/%s smart upgrade -y' % (SYSROOT, self.next_rootfs))

            os.system('cp -r %s/%s/var/lib/rpm/* %s/' % (SYSROOT, self.next_rootfs, tempd))
            os.system('umount %s/%s/var/lib/rpm' % (SYSROOT, self.next_rootfs))
            os.system('cp -r %s/* %s/%s/var/lib/rpm/' % (tempd, SYSROOT, self.next_rootfs))
            os.system('rm -rf %s' % tempd)

            os.system('umount %s/%s/run' % (SYSROOT, self.next_rootfs))
            os.system('umount %s/%s/dev' % (SYSROOT, self.next_rootfs))
            os.system('umount %s/%s/proc' % (SYSROOT, self.next_rootfs))

            if result != 0:
                self.message = 'Error: System update failed! It has no effect on the running system!'
                self.message += '\n'
                self.message += 'Please do not run rollback!'
                return False
                # sys.exit(2)
            
            upgrade_bzImage = '%s/%s/%s' % (SYSROOT, self.next_rootfs, self.kernel)
            if os.path.islink(upgrade_bzImage):
                upgrade_kernel = '%s/%s/%s' % (SYSROOT, self.next_rootfs, os.path.realpath(upgrade_bzImage))
            else:
                upgrade_kernel = upgrade_bzImage

            upgrade_kernel_md5 = ''
            if os.path.exists(upgrade_kernel):
                upgrade_kernel_md5 = self._compute_checksum(upgrade_kernel)

            if upgrade_kernel_md5 and self.kernel_md5 != upgrade_kernel_md5:
                #backup kernel
                os.system('cp -f %s  %s_bakup' % (self.kernel, self.kernel))
                os.system('cp -f %s %s' % (upgrade_kernel, self.kernel))

            #if grub-efi exists, replace the old one with it in case they are upgraded also
            if os.path.exists('%s/%s/boot/efi/EFI/BOOT' % (SYSROOT, self.next_rootfs)):
                self.message += "Replace the grub-efi related files with the latest one \n"
                #Here we only update the efi binary files, excluding the grub.cfg and startup.nsh
                os.system('cp -rf %s/%s/boot/efi/EFI/BOOT/*efi /boot/EFI/BOOT/' % (SYSROOT, self.next_rootfs))

            #setup default subvolume
            upgrade_subvolid = self._get_btrfs_value('%s/%s' % (SYSROOT, self.next_rootfs), 'Subvolume ID')
            argv = 'subvolume set-default %s %s' % (upgrade_subvolid, SYSROOT)
            self._btrfs(argv)
        else:
            self.message = "Error: You are running in the backup mode, cannot do upgrade!"
            return False
            # sys.exit(2)
        self.message = "do upgrade ok"
        return True
             
    def do_upgrade(self):
            self._mount_rootvolume()
            self._do_upgrade()

    def do_rollback(self):
        if self.bakup_mode:
            self.message = "Error: You are running in the backup mode, cannot do rollback!"
            return False
            # sys.exit(2)
        bzImage = {'normal':'bzImage_bakup', 'bakup':'bzImage'}

        self._mount_rootvolume()
        if not os.path.exists('%s/%s' % (SYSROOT, self.next_rootfs)):
            self.message = "Error: There is no previous status to rollback to!"
            return False
            # sys.exit(2)

        rollback_kernel = '%s/%s/%s' % (SYSROOT, self.next_rootfs, self.kernel)
        rollback_kernel_md5 = ''
        if os.path.exists(rollback_kernel):
            if os.path.islink(rollback_kernel):
                rollback_kernel = '%s/%s/%s' % (SYSROOT, self.next_rootfs, os.path.realpath(rollback_kernel))
        else:
            rollback_kernel = '%s_bakup' % self.kernel

        upgrade_kernel_md5 = self._compute_checksum(rollback_kernel)

        if rollback_kernel_md5 != self.kernel_md5:
            os.system('cp -f %s %s' % (rollback_kernel, self.kernel))

        #if grub-efi exists, rollback it too
        if os.path.exists('%s/%s/boot/efi/EFI/BOOT' % (SYSROOT, self.next_rootfs)):
            self.message += "Rollback grub-efi related files \n"
            #Here we only rollback the efi binary files, excluding the grub.cfg and startup.nsh
            os.system('cp -rf %s/%s/boot/efi/EFI/BOOT/*efi /boot/EFI/BOOT/' % (SYSROOT, self.next_rootfs))

        #setup default subvolume
        rollback_subvolid = self._get_btrfs_value('%s/%s' % (SYSROOT, self.next_rootfs), 'Subvolume ID')
        argv = 'subvolume set-default %s %s' % (rollback_subvolid, SYSROOT)
        self._btrfs(argv)

        self.message = "rollback succeeded"
        return True
        


