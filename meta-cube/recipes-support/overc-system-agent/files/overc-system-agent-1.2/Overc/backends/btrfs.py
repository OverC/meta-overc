import sys, os
import tempfile
import subprocess
from Overc.utils  import Utils
from Overc.utils  import CONTAINER_MOUNT
from Overc.utils  import FACTORY_SNAPSHOT
from Overc.utils  import SYSROOT

class Btrfs(Utils):
    def __init__(self):
        self.name = None
        self.rootfs = None
        self.message = ""
        self.rootdev = self._getrootdev("/", True)
        self.bakup_mode = self._get_bootmode()
        self.rootfs_dic = {'rootfs':'rootfs_upgrade', 'rootfs_upgrade':'rootfs'}
                
        if self._path_exists('/boot/bzImage', True):
            self.kernel = "/boot/bzImage"
        elif self._path_exists('/boot/uImage', True):
            self.kernel = "/boot/uImage"
        elif self._path_exists('/boot/zImage', True):
            self.kernel = "/boot/zImage"
        elif self._path_exists('/boot/fitImage', True):
            self.kernel = "/boot/fitImage"
        elif self._path_exists('/boot/kernel7.img', True):
            self.kernel = "/boot/kernel7.img"

        self.kernel_md5 = self._compute_checksum(self.kernel, True)

        if not self.rootdev or not self.rootfs:
            sys.exit(2)

        self.next_rootfs = self.rootfs_dic[self.rootfs]

    def _path_exists(sef, path, host=False):
        rc = True
        if host:
            try:
                subprocess.check_output("cube-cmd test -e %s" % path, shell=True)
            except subprocess.CalledProcessError:
                rc = False
        else:
            rc = os.path.exists(path)
        return rc

    def _mkdirhier(self, directory, host=False):
        if host:
            subprocess.check_call("cube-cmd mkdir -p %s" % directory, shell=True)
        else:
            try:
                os.makedirs(directory)
            except OSError as e:
                if e.errno != errno.EEXIST:
                    raise e

    def _rmdir(self, directory, host=False):
        if host:
            return subprocess.check_call("cube-cmd rmdir %s" % directory, shell=True)
        else:
            return os.rmdir(directory)

    def _mkdtemp(self, directory, host=False):
        if host:
            return subprocess.check_output("cube-cmd mktemp -d -p %s" % directory, shell=True).decode("utf-8").strip('\n')
        else:
            return tempfile.mkdtemp(dir=directory)

    def _host_copyfile(self, source, dest):
        cp_fd, cp_script = tempfile.mkstemp(dir="/var/lib/cube/local/")
        with open(cp_fd, "w") as script:
            script.write("cp -rf %s %s" % (source, dest))
        os.system("cube-cmd bash %s" % cp_script)
        os.system("rm %s" % cp_script)

    def _btrfs(self, args, host=False):
        cube_cmd = "cube-cmd " if host else ""
        return os.system("%s/usr/bin/btrfs %s" % (cube_cmd, args))

    def _getrootdev(self, mountdir, host=False):
        cube_cmd = "cube-cmd " if host else ""
        subp=subprocess.Popen('%smount' % cube_cmd, shell=True, stdout=subprocess.PIPE)
        c=subp.stdout.readline().decode("utf-8").strip()
        while c:
            c_list = c.split()
            if  c_list[2] == mountdir:
                return c_list[0]
            c=subp.stdout.readline().decode("utf-8").strip()

        return None
    
    def _mount_rootvolume(self, host=False):
        if not self._path_exists(SYSROOT, host):
            self._mkdirhier(SYSROOT, host)

        cube_cmd = "cube-cmd " if host else ""
        subp=subprocess.Popen('%smount' % cube_cmd, shell=True, stdout=subprocess.PIPE)
        c=subp.stdout.readline().decode("utf-8").strip()
        need_mount = True
        while c:
            c_list = c.split()
            if c_list[0] == self.rootdev and c_list[2] == SYSROOT:
                #root volume has been mount to SYSROOT
                need_mount = False
                break
            c=subp.stdout.readline().decode("utf-8").strip()
            
        if need_mount:
            os.system('%smount -o subvolid=5 %s %s' % (cube_cmd, self.rootdev, SYSROOT))

    def _get_bootmode(self):
        self.rootfs = self._get_btrfs_value("/", "Name", True)
        if self.rootfs == "rootfs_bakup":
            return True
        else:
            return False

    def _get_btrfs_value(self, path, key, host=False):
        cube_cmd = "cube-cmd " if host else ""
        subp=subprocess.Popen('%s/usr/bin/btrfs subvolume show %s' % (cube_cmd, path), shell=True,stdout=subprocess.PIPE)
        c=subp.stdout.readline().decode("utf-8").strip()
        while c:
            c_list = c.split(':')
            if  c_list[0].strip() == key:
                return c_list[1].strip()
                
            c=subp.stdout.readline().decode("utf-8").strip()

    def _cleanup_subvol(self, subvoldir, exclude, host=False):
        cube_cmd = "cube-cmd " if host else ""
        subp=subprocess.Popen('%s/usr/bin/btrfs subvolume list %s' % (cube_cmd, subvoldir), shell=True, stdout=subprocess.PIPE)
        c=subp.stdout.readline().decode("utf-8").strip()
        subvol_stack = []
        while c:
            c_list = c.split()
            subvolid = c_list[-1].split('/')[0]
            if  subvolid != self.rootfs and subvolid not in exclude:
                subvol = 'subvolume delete -C %s/%s' % (subvoldir, c_list[-1])
                subvol_stack.append(subvol)
            c=subp.stdout.readline().decode("utf-8").strip()

        while subvol_stack:
            self._btrfs(subvol_stack.pop(), host)

    def clean_essential(self):
        self._mount_rootvolume(True)
        self._cleanup_subvol(SYSROOT, [FACTORY_SNAPSHOT, 'rootfs_bakup'], True)

    def clean_container(self):
        self._mount_container_root(True)
        current_subvol = self._get_btrfs_value(CONTAINER_MOUNT, 'Name', True)
        self._cleanup_subvol('%s/.tmp' % CONTAINER_MOUNT, [current_subvol, FACTORY_SNAPSHOT], True)
        os.system('cube-cmd umount %s/.tmp' % CONTAINER_MOUNT)

    def factory_reset(self):
        return self._factory_reset_essential() and self._factory_reset_container() 

    def _factory_reset_essential(self):
        self._mount_rootvolume(True)
        #first remove the snapshot of unused backup rootfs
        self._cleanup_subvol(SYSROOT, [FACTORY_SNAPSHOT], True)

        #check is the subvolumes are cleaned up
        if self._path_exists('%s/%s' % (SYSROOT, self.next_rootfs), True):
            self.message = 'Cannot cleanup the subvolumes in root tree %s. There must be something wrong!' % SYSROOT
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            return False

        ret = self._btrfs('subvolume snapshot %s/%s %s/%s' % (SYSROOT, FACTORY_SNAPSHOT, SYSROOT, self.next_rootfs), True)
        if ret != 0:
            self.message += "Cannot factory reset, please check is there enough diskspace left"
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            return False

        #create snapshot for backup boot entry
        self._btrfs('subvolume snapshot %s/%s %s/rootfs_bakup' % (SYSROOT, FACTORY_SNAPSHOT, SYSROOT), True)

        #setup default subvolume
        subvolid = self._get_btrfs_value('%s/%s' % (SYSROOT, self.next_rootfs), 'Subvolume ID', True)
        argv = 'subvolume set-default %s %s' % (subvolid, SYSROOT)
        if not self._btrfs(argv, True):
            #factory-reset the kernel image
            self.message += "factory reset kernel %s \n" % self.kernel
            real_kernel = subprocess.check_output("cube-cmd realpath %s/%s/%s" % (SYSROOT, FACTORY_SNAPSHOT, self.kernel), shell=True).decode("utf-8").strip("\n")
            os.system('cube-cmd cp -f %s %s' % (real_kernel, self.kernel))
            if self._path_exists(real_kernel + '.p7b', True):
                os.system('cube-cmd cp -f %s.p7b %s.p7b' % (real_kernel, self.kernel))

            #if grub-efi exists, factory-reset those files from the original rootfs too.
            if self._path_exists('%s/%s/boot/EFI/BOOT' % (SYSROOT, FACTORY_SNAPSHOT), True):
                self.message += "factory reset grub-efi related files \n"
                #Here we only reback the efi binary files, excluding the grub.cfg and startup.nsh
                self._host_copyfile('%s/%s/boot/EFI/BOOT/*efi' % (SYSROOT, FACTORY_SNAPSHOT), '/boot/EFI/BOOT/')
            return True
        else:
            self.message += "Cannot set default mount subvolume to %s" % self.next_rootfs
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            return False
   
    def _mount_container_root(self, host=False):
        cube_cmd = "cube-cmd " if host else ""
        if not self._path_exists('%s/.tmp' % CONTAINER_MOUNT, host):
            self._mkdirhier('%s/.tmp' % CONTAINER_MOUNT, host)
        devpath = self._getrootdev(CONTAINER_MOUNT, host)
        os.system('%smount -o subvolid=5 %s %s/.tmp' % (cube_cmd, devpath, CONTAINER_MOUNT))

    def _factory_reset_container(self):
        self._mount_container_root(True)
        if not self._path_exists('%s/.tmp/%s' % (CONTAINER_MOUNT, FACTORY_SNAPSHOT), True):
            self.message += 'Error: Cannot find the snapshot of factory in %s/.tmp' % CONTAINER_MOUNT
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            os.system('cube-cmd umount %s/.tmp' % CONTAINER_MOUNT)
            return False
        work_subvol =  self._random_str()   
        workdir = '%s/.tmp/%s' % (CONTAINER_MOUNT, work_subvol)
        ret = self._btrfs('subvolume snapshot %s/.tmp/%s %s' % (CONTAINER_MOUNT, FACTORY_SNAPSHOT, workdir), True)
        if ret != 0:
            self.message += "Cannot factory reset, please check is there enough diskspace left"
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            os.system('cube-cmd umount %s/.tmp' % CONTAINER_MOUNT)
            return False

        #snapshot the children subvolumes
        subp=subprocess.Popen('cube-cmd /usr/bin/btrfs subvolume list -o %s/.tmp/%s' % (CONTAINER_MOUNT, FACTORY_SNAPSHOT), shell=True, stdout=subprocess.PIPE)
        c=subp.stdout.readline().decode("utf-8").strip()
        while c:
            c_list = c.split()
            factory_subvol = c_list[-1]
            workdir_subvol = factory_subvol.replace(FACTORY_SNAPSHOT, '')
            snapshot_dir = '%s/%s' % (workdir, workdir_subvol)
            if self._path_exists(snapshot_dir, True):
                self._rmdir(snapshot_dir, True)
            ret = self._btrfs('subvolume snapshot %s/.tmp/%s %s' % (CONTAINER_MOUNT, factory_subvol, snapshot_dir), True)
            if ret != 0:
                self.message += 'Cannot snapshot subvolume of %s.tmp/%s' % (CONTAINER_MOUNT, factory_subvol)
                self.message += '\nto %s' % snapshot_dir
                self.message += 'Factory reset aborted!'
                return False
            
            c=subp.stdout.readline().decode("utf-8").strip()
        
        #setup default subvolume
        subvolid = self._get_btrfs_value(workdir, 'Subvolume ID', True)
        argv = 'subvolume set-default %s %s' % (subvolid, CONTAINER_MOUNT)
        if not self._btrfs(argv, True):
            os.system('cube-cmd umount %s/.tmp' % CONTAINER_MOUNT)
            return True
        else:
            self.message += "Cannot set default mount subvolume to %s" % workdir
            self.message += '\n'
            self.message += 'Factory reset aborted!'
            os.system('cube-cmd umount %s/.tmp' % CONTAINER_MOUNT)
            return False
   
    def _do_upgrade(self, host=False):
        cube_cmd = "cube-cmd " if host else ""
        #first remove the previous rootfs and bakup current running rootfs
        self._cleanup_subvol(SYSROOT, [FACTORY_SNAPSHOT], host)
        bzImage = {'normal':'bzImage_bakup', 'bakup':'bzImage'}

        if self._path_exists('%s/%s' % (SYSROOT, self.next_rootfs), host):
            self.message = 'Cannot cleanup the subvolumes in root tree %s. There must be something wrong!' % SYSROOT
            self.message += '\n'
            self.message += 'Upgrade aborted!'
            return False
            # sys.exit(2)

        if not self.bakup_mode:
            argv = 'subvolume snapshot %s/%s %s/rootfs_bakup' % (SYSROOT, self.rootfs, SYSROOT)
            self._btrfs(argv, host)

            #setup upgrade rootfs
            argv = 'subvolume snapshot %s/%s %s/%s' % (SYSROOT, self.rootfs, SYSROOT, self.next_rootfs)
            self._btrfs(argv, host)

            #do upgrade
            os.system('%smount -t proc proc %s/%s/proc' % (cube_cmd, SYSROOT, self.next_rootfs))
            os.system('%smount -o bind /dev %s/%s/dev' % (cube_cmd, SYSROOT, self.next_rootfs))
            os.system('%smount --bind /run %s/%s/run' % (cube_cmd, SYSROOT, self.next_rootfs))

            tempd = self._mkdtemp('%s/%s/tmp' % (SYSROOT, self.next_rootfs), host)
            self._host_copyfile('%s/%s/var/lib/rpm/*' % (SYSROOT, self.next_rootfs), tempd)
            os.system('%smount -t tmpfs tmpfs %s/%s/var/lib/rpm' % (cube_cmd, SYSROOT, self.next_rootfs))
            self._host_copyfile('%s/*' % tempd, '%s/%s/var/lib/rpm/' % (SYSROOT, self.next_rootfs))

            result = os.system('%schroot %s/%s dnf -y upgrade --refresh' % (cube_cmd, SYSROOT, self.next_rootfs))

            self._host_copyfile('%s/%s/var/lib/rpm/*' % (SYSROOT, self.next_rootfs), tempd)
            os.system('%sumount %s/%s/var/lib/rpm' % (cube_cmd, SYSROOT, self.next_rootfs))
            self._host_copyfile('%s/*' % tempd, '%s/%s/var/lib/rpm/' % (SYSROOT, self.next_rootfs))
            os.system('%srm -rf %s' % (cube_cmd, tempd))

            os.system('%sumount %s/%s/run' % (cube_cmd, SYSROOT, self.next_rootfs))
            os.system('%sumount %s/%s/dev' % (cube_cmd, SYSROOT, self.next_rootfs))
            os.system('%sumount %s/%s/proc' % (cube_cmd, SYSROOT, self.next_rootfs))

            if result != 0:
                self.message = 'Error: System update failed! It has no effect on the running system!'
                self.message += '\n'
                self.message += 'Please do not run rollback!'
                return False
                # sys.exit(2)

            upgrade_kernel = subprocess.check_output("%srealpath %s/%s/%s" % (cube_cmd, SYSROOT, self.next_rootfs, self.kernel), shell=True).decode("utf-8").strip("\n")
            upgrade_kernel_md5 = ''
            if self._path_exists(upgrade_kernel, host):
                upgrade_kernel_md5 = self._compute_checksum(upgrade_kernel, host)

            if upgrade_kernel_md5 and self.kernel_md5 != upgrade_kernel_md5:
                #backup kernel
                os.system('%scp -f %s  %s_bakup' % (cube_cmd, self.kernel, self.kernel))
                if self._path_exists(self.kernel + '.p7b', host):
                    os.system('%scp -f %s.p7b %s_bakup.p7b' % (cube_cmd, self.kernel, self.kernel))
                os.system('%scp -f %s %s' % (cube_cmd, upgrade_kernel, self.kernel))
                if self._path_exists(upgrade_kernel + '.p7b', host):
                    os.system('%scp -f %s.p7b %s.p7b' % (cube_cmd, upgrade_kernel, self.kernel))

            #if grub-efi exists, replace the old one with it in case they are upgraded also
            if self._path_exists('%s/%s/boot/EFI/BOOT' % (SYSROOT, self.next_rootfs), host):
                self.message += "Replace the grub-efi related files with the latest one \n"
                #Here we only update the efi binary files, excluding the grub.cfg and startup.nsh
                os.system('%scp -rf %s/%s/boot/EFI/BOOT/*efi /boot/EFI/BOOT/' % (cube_cmd, SYSROOT, self.next_rootfs))

            #setup default subvolume
            upgrade_subvolid = self._get_btrfs_value('%s/%s' % (SYSROOT, self.next_rootfs), 'Subvolume ID', host)
            argv = 'subvolume set-default %s %s' % (upgrade_subvolid, SYSROOT)
            self._btrfs(argv, host)
        else:
            self.message = "Error: You are running in the backup mode, cannot do upgrade!"
            return False
            # sys.exit(2)
        self.message = "do upgrade ok"
        return True
             
    def do_upgrade(self):
            self._mount_rootvolume(True)
            self._do_upgrade(True)

    def do_rollback(self):
        if self.bakup_mode:
            self.message = "Error: You are running in the backup mode, cannot do rollback!"
            return False
            # sys.exit(2)
        bzImage = {'normal':'bzImage_bakup', 'bakup':'bzImage'}

        self._mount_rootvolume(True)
        if not self._path_exists('%s/%s' % (SYSROOT, self.next_rootfs), True):
            self.message = "Error: There is no previous status to rollback to!"
            return False
            # sys.exit(2)

        rollback_kernel = '%s/%s/%s' % (SYSROOT, self.next_rootfs, self.kernel)
        rollback_kernel_md5 = ''
        if self._path_exists(rollback_kernel, True):
            rollback_kernel = subprocess.check_output("cube-cmd realpath %s/%s/%s" % (SYSROOT, self.next_rootfs, self.kernel), shell=True).decode("utf-8").strip('\n')
        else:
            rollback_kernel = '%s_bakup' % self.kernel

        upgrade_kernel_md5 = self._compute_checksum(rollback_kernel, True)

        if rollback_kernel_md5 != self.kernel_md5:
            os.system('cube-cmd cp -f %s %s' % (rollback_kernel, self.kernel))
            if self._path_exists(rollback_kernel + '.p7b', True):
                os.system('cube-cmd cp -f %s.p7b %s.p7b' % (rollback_kernel, self.kernel))

        #if grub-efi exists, rollback it too
        if self._path_exists('%s/%s/boot/EFI/BOOT' % (SYSROOT, self.next_rootfs), True):
            self.message += "Rollback grub-efi related files \n"
            #Here we only rollback the efi binary files, excluding the grub.cfg and startup.nsh
            self._host_copyfile('%s/%s/boot/EFI/BOOT/*efi' % (SYSROOT, self.next_rootfs), '/boot/EFI/BOOT/')

        #setup default subvolume
        rollback_subvolid = self._get_btrfs_value('%s/%s' % (SYSROOT, self.next_rootfs), 'Subvolume ID', True)
        argv = 'subvolume set-default %s %s' % (rollback_subvolid, SYSROOT)
        self._btrfs(argv, True)

        self.message = "rollback succeeded"
        return True
