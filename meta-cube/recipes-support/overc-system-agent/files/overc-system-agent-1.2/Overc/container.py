import sys, os
import subprocess
import os.path
from Overc.utils import Process

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

    def upgrade(self, name, template, rpm_upgrade=True, image_upgrade=False):
        if image_upgrade and not rpm_upgrade:
            retval = self.update(template)
            if retval is 0:
                msg1 = self.message
                force = True
                retval = self.activate(name, template, force)
                msg2 = self.message
                self.message = msg1 + "\n" + msg2
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
        retval = self.run_script(template, args)
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
        cmd = "cube-cmd"
        p = subprocess.Popen([cmd,'lxc-attach', '-n', name, 'cat', '/etc/issue'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        issue_s = p.stdout.readline()
        while p.stdout.readline(): #drain out of the pipe
            pass
        return issue_s

    def get_container(self, template):
        cmd = "cube-cmd"
        p = subprocess.Popen([cmd,'lxc-ls'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        cn=p.stdout.readline() #abandon the first line

        return cn.split()

    def is_active(self, cn, template):
        args = "-A -n %s" % cn
        if self.run_script(template, args, True) is 3:
            return True
        else:
            return False

    def run_script(self, template, args, failok=False):
        fname = CONTAINER_SCRIPT_PATH + "/" + template
        if not os.path.isfile(fname):
            self.message = "Error! Missing file: %s" % fname
            return 1
        cmd = "%s %s" % (fname, args)
        process = Process()
        retval = process.run(cmd)
        self.message = process.message
        if retval is 0:
            print "%s ok" % fname
        elif not failok:
                print "Error! %s failed" % fname
        return retval

