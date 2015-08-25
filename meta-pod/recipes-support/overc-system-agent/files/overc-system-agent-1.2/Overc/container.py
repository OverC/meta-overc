import sys, os
import os.path
import subprocess

# containers template named scripts
CONTAINER_SCRIPT_PATH = "/etc/overc/container/"

class Container(object):
    def __init__(self):
        pass

    def activate(self, name, template, force_create):
        args = "-A %s" % name
        container_status = self.run_script(template, args, True)
        if container_status == 3:
            if force_create:
                print "Stopping container %s" % name
                retval = self.stop(name, template)
                if retval != 0:
                    self.message = "Error! Cannot stop container %s" % name
                    return
            else:
                self.message = "Error! %s container is active" % name
                return
        elif container_status == 1:
            self.message = "Error! Acquiring container status failed"
            return container_status

        args = "-a %s" % name
        retval = self.run_script(template, args)
        if retval != 0:
            self.message = "Activate failed"
        return retval

    def rollback(self, name, snapshot_name, template):
        args = "-A %s" % name
        container_status = self.run_script(template, args, True)
        if container_status == 3:
            self.message = "Error! %s container is active" % name
            return
        elif container_status == "0":
            self.message = "Error! %s container does not exist" % name
            return
        elif container_status == 1:
            self.message = "Error! Acquiring container status failed"
            return container_status

        args = "-R %s %s" % (name, snapshot_name)
        retval = self.run_script(template, args)
        if retval is 0:
            self.message = "Rollback ok"
        else:
            self.message = "Rollback failed"
        return retval
 
    def start(self, name, template):
        args = "-S %s" % name
        retval = self.run_script(template, args)
        if retval is 0:
            self.message = "Start ok"
        else:
            self.message = "Start failed"
        return retval
 
    def stop(self, name, template):
        args = "-K %s" % name
        retval = self.run_script(template, args)
        if retval is 0:
            self.message = "Stop ok"
        else:
            self.message = "Stop failed"
        return retval
 
    def send_image(self, template, image_url):
        args = "-s %s" % image_url
        retval = self.run_script(template, args)
        if retval is 0:
            self.message = "Send Image ok"
        else:
            self.message = "Send Image failed"
        return retval

    def update(self, template):
        args = "-U"
        retval = self.run_script(template, args)
        if retval is 0:
            self.message = "Upgrade ok"
        else:
            self.message = "Upgrade failed"
        return retval

    def list(self, template):
        args = "-L"
        retval = self.run_script(template, args)
        if retval != 0:
            self.message = "List failed"
        return retval

    def list_snapshot(self, name, template):
        args = "-B %s" % name
        retval = self.run_script(template, args)
        return retval
        if retval != 0:
            self.message = "List snapshot failed"
        return retval

    def run_script(self, template, args, failok=False):
        fname = CONTAINER_SCRIPT_PATH + "/" + template
        if not os.path.isfile(fname):
            self.message = "Error! Missing file: %s" % fname
            return 1
        cmd = "%s %s" % (fname, args)
        print "Running: %s" % cmd
        child  = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)
        stdout = child.communicate()[0]
        self.message = stdout
        if child.returncode is 0:
            print "%s ok" % fname
        elif not failok:
            print "Error! %s failed" % fname
        return child.returncode
