import sys, os
import os.path
import subprocess
import select

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

    def rollback(self, name, snapshot_name, template):
        args = "-R -n %s" % name
        if snapshot_name is not None:
            args += " -b %s" % snapshot_name
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

    def upgrade(self, name, template, rpm_upgrade=False):
        if rpm_upgrade:
            args = "-r -n %s" % name
            retval = self.run_script(template, args)
        else:
            retval = self.update(template)
            if retval is 0:
                msg1 = self.message
                force = True
                retval = self.activate(name, template, force)
                msg2 = self.message
                self.message = msg1 + "\n" + msg2
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

    def run_script(self, template, args, failok=False):
        fname = CONTAINER_SCRIPT_PATH + "/" + template
        if not os.path.isfile(fname):
            self.message = "Error! Missing file: %s" % fname
            return 1
        cmd = "%s %s" % (fname, args)
        print "Running: %s" % cmd

        child = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        self.message = ''
        while True:
            fds = select.select([child.stdout.fileno(), child.stderr.fileno()], [], [])

            for fd in fds[0]:
                if fd == child.stdout.fileno():
                    read = child.stdout.readline()
                    if read != '':
                        sys.stdout.write(read)
                    self.message += read
                if fd == child.stderr.fileno():
                    read = child.stderr.readline()
                    if read != '':
                        sys.stderr.write(read)
                    self.message += read
            if child.poll() != None:
                break
        rc = child.poll()
        if rc is 0:
            print "%s ok" % fname
        elif not failok:
            print "Error! %s failed" % fname
        return rc
