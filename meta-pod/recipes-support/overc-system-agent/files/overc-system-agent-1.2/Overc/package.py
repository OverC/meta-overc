import sys, os
import subprocess
import select

class Package(object):
    def __init__(self):
        pass

    def _smartpm(self, args, chroot=None):
        cmd = []
        if chroot != None:
            cmd.append("chroot")
            cmd.append(chroot)
        cmd.append("/usr/bin/smart")
        cmd.append(args)
        print "Running: %s" % ' '.join(cmd)
        child = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
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
        if rc != 0:
            print "Error!: %s" % ' '.join(cmd)
 
    def _get_kernel(self, path):
        if path == "/":
            subp=subprocess.Popen("rpm -qa | grep kernel-image | xargs rpm -ql | grep bzImage | awk -F'/' '{print $3}'", shell=True,stdout=subprocess.PIPE)
        else:
            subp1 = subprocess.Popen("chroot %s rpm -qa | grep kernel-image" % path, shell=True,stdout=subprocess.PIPE)
            rpm_package = subp1.stdout.readline().strip()
            subp=subprocess.Popen("chroot %s rpm -ql %s | grep bzImage | awk -F'/' '{print $3}'" % (path, rpm_package), shell=True,stdout=subprocess.PIPE)

        c=subp.stdout.readline().strip()
        return c

