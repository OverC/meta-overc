import sys, os
import subprocess

class Package(object):
    def __init__(self):
        pass

    def _smartpm(self, args):
        # return os.system('/usr/bin/smart %s' % args)
        self.message = subprocess.Popen(["/usr/bin/smart", args], stdout=subprocess.PIPE).communicate()[0]
 
    def _get_kernel(self, path):
        if path == "/":
            subp=subprocess.Popen("rpm -qa | grep kernel-image | xargs rpm -ql | grep bzImage | awk -F'/' '{print $3}'", shell=True,stdout=subprocess.PIPE)
        else:
            subp1 = subprocess.Popen("chroot %s rpm -qa | grep kernel-image" % path, shell=True,stdout=subprocess.PIPE)
            rpm_package = subp1.stdout.readline().strip()
            subp=subprocess.Popen("chroot %s rpm -ql %s | grep bzImage | awk -F'/' '{print $3}'" % (path, rpm_package), shell=True,stdout=subprocess.PIPE)

        c=subp.stdout.readline().strip()
        return c

