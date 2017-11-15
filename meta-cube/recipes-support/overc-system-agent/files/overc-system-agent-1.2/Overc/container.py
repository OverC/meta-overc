import sys, os
import subprocess
import os.path
from Overc.utils import Process
from Overc.logger import logger as log
from Overc.utils  import CONTAINER_MOUNT

# containers template named scripts
CONTAINER_SCRIPT_PATH = "/etc/overc/container/"

class Container(object):
    def __init__(self):
        pass

    def activate(self, name, template, force_create):
        args = "-a -n %s" % name
        if force_create:
            args += " -f"
        retval = self.run_script(template, args)
        if retval != 0:
            self.message += "\nActivate failed"
        else:
            self.message += "\nActivate ok"
        return retval

    def rollback(self, name, snapshot_name, template, force):
        args = "-R -n %s" % name
        if snapshot_name is not None:
            args += " -b %s" % snapshot_name
        if force:
            args += " -f"
        retval = self.run_script(template, args)
        if retval is 0:
            self.message += "\nRollback ok"
        else:
            self.message += "\nRollback failed"
        return retval
 
    def start(self, name, template):
        args = "-S -n %s" % name
        retval = self.run_script(template, args)
        if retval is 0:
            self.message += "\nStart ok"
        else:
            self.message += "\nStart failed"
        return retval
 
    def stop(self, name, template):
        args = "-K -n %s" % name
        retval = self.run_script(template, args)
        if retval is 0:
            self.message += "\nStop ok"
        else:
            self.message += "\nStop failed"
        return retval
 
    def send_image(self, template, image_url):
        args = "-s -u %s" % image_url
        retval = self.run_script(template, args)
        if retval is 0:
            self.message += "\nSend Image ok"
        else:
            self.message += "\nSend Image failed"
        return retval

    def update(self, template):
        args = "-U"
        retval = self.run_script(template, args)
        if retval is 0:
            self.message += "\nUpdate ok"
        else:
            self.message += "\nUpdate failed"
        return retval

    def get_overlay(self, name):
        # FIXME: we do not support overlay so far, so disable it
        return []

        fstabfile="%s/%s/fstab" % (CONTAINER_MOUNT,name)
        fstab=open(fstabfile, 'r')
        dirlist=[]
        for line in fstab:
            member=line.split()
            if member[0]=="overlay":
                strpre="%s/%s/rootfs" % (CONTAINER_MOUNT,name)
                strdir=member[1][member[1].find(strpre) + len(strpre):len(member[1])]
                dirlist.append(strdir)
        return dirlist

    def is_overlay(self, name):
        dirlist=self.get_overlay(name)
        return len(dirlist)

    def upgrade(self, name, template, rpm_upgrade=True, image_upgrade=False, skip_del=False):
        if image_upgrade and not rpm_upgrade:
            retval = self.update(template)
            if retval is 0:
                msg1 = self.message
                force = True
                retval = self.activate(name, template, force)
                msg2 = self.message
                self.message = msg1 + "\n" + msg2
        else:
            if skip_del:
                args = "-r -n %s -f" % name
            else:
                args = "-r -n %s" % name
            retval = self.run_script(template, args)

        if retval is 0:
            self.message += "\nUpgrade ok"
        else:
            self.message += "\nUpgrade failed"
        return retval

    def list(self, template):
        args = "-L"
        retval = self.run_script(template, args, liveoutput=False)
        if retval != 0:
            self.message += "\nList failed"
        return retval

    def list_snapshot(self, name, template):
        args = "-B -n %s" % name
        retval = self.run_script(template, args)
        if retval != 0:
            self.message += "\nList snapshot failed"
        return retval

    def snapshot(self, name, template):
        args = "-p -n %s" % name
        retval = self.run_script(template, args)
        if retval != 0:
            self.message += "\nSnapshot failed"
        return retval

    def delete(self, name, template, force_delete):
        args = "-d -n %s" % name
        if force_delete:
            args += " -f"
        retval = self.run_script(template, args)
        if retval is 0:
            self.message += "\nDelete ok"
        else:
            self.message += "\nDelete failed"
        return retval

    def delete_snapshots(self, name, template):
        args = "-D -n %s" % name
        retval = self.run_script(template, args)
        if retval is 0:
            self.message += "\nDelete snapshots ok"
        else:
            self.message += "\nDelete snapshots failed"
        return retval

    def get_issue(self, name, template):
        cmd = "cube-ctl %s:'cat /etc/issue'" % name
        return subprocess.check_output(cmd, shell=True).decode("utf-8").strip("\n")

    def get_container(self, template):
        cmd = "cube-ctl list | tail -2 | awk '{ print $1 }'"
        return subprocess.check_output(cmd, shell=True).decode("utf-8").strip("\n").split()

    def is_active(self, cn, template):
        args = "-A -n %s" % cn
        if self.run_script(template, args, True) is 3:
            return True
        else:
            return False

    def run_script(self, template, args, failok=False, liveoutput=True):
        fname = CONTAINER_SCRIPT_PATH + "/" + template
        if not os.path.isfile(fname):
            self.message = "Error! Missing file: %s" % fname
            return 1
        cmd = "%s %s" % (fname, args)
        process = Process()
        retval = process.run(cmd, liveoutput)
        self.message = process.message
        if retval is 0:
            log.info("%s ok" % fname)
        elif not failok:
            log.error("%s failed" % fname)
        return retval

    def _overlay(self, cn, dirs, restore, sources=None):
        # Pararmeter check
        retval = 0
        if (dirs == None):
            log.error("No dirs in parameter")
            return -1
        if (restore == False):
            if (sources == None):
                log.error("No sources in parameter")
                return -1
            else: # Check source container name
                for cn0 in sources.split(','):
                    if (cn0 == cn):
                        log.error("Can not set same container in source list")
                        return -1

        # check if overlay dir exists, ex /var/lib/lxc/dom0/rootfs/usr_temp
        for oldir in dirs.split(','):
            temppath="%s/%s/rootfs%s_temp" % (CONTAINER_MOUNT,cn,oldir)
            fullpath="%s/%s/rootfs%s" % (CONTAINER_MOUNT,cn,oldir)
            if (restore == True): # Stop an overlay,
                if (os.path.isdir(temppath) == False): #no such dir
                    log.error("%s:not an overlay-ed dir in container" % (oldir))
                    return -1
            else: # Create an overlay
                if (os.path.isdir(temppath) == True): # already overlay dir
                    log.error("%s:already an overlay-ed dir in container" % (oldir))
                    return -1
        # Insert request into lxc.service
        lxcfile = '%s/overlayrestore' % (CONTAINER_MOUNT)
        lxc = open(lxcfile, 'a+')
        lines=lxc.readlines().decode("utf-8")
        found = 0
        for oldir in dirs.split(','):
            basepara = "%s %s" % (cn,oldir)
            for line in lines:
                if (line.find(basepara) != -1):
                    found = 1
                    break
            if (found == 0):
                if (restore == True):
                    cmdline = "/etc/lxc/overlayrestore %s\n" % basepara
                else:
                    cmdline = "/etc/lxc/overlaycreate %s %s\n" % (basepara, sources)
                lxc.write(cmdline)
                retval = 1
            else:
                log.info("%s already in overlay rebuild list, ignored" % basepara)
        lxc.close()
        return retval

    def overlay_create(self, cn, dirs, source):
        val = self._overlay(cn, dirs, False, source)
        if (val == -1):
            return -1
        if (val == 0):
            return 0
        if (val == 1):
            log.info("Reboot required to rebuild overlay directories")
            return 0

    def overlay_stop(self, cn, dirs):
        # Checking container status, overlay available
        val = self._overlay(cn, dirs, True)
        if (val == -1):
            return -1
        if (val == 0):
            return 0
        if (val == 1):
            log.info("Reboot required to rebuild overlay directories")
            return 0
