import sys, os 
from Overc.utils import Process 

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
        cmd_s = ' '.join(cmd)
        process = Process()
        retval = process.run(cmd_s)
        self.message = process.message
        if retval is not 0:
            print "Error!: %s" % cmd_s
        return retval
 
    def _get_kernel(self, path):
        if path == "/":
            subp=subprocess.Popen("rpm -qa | grep kernel-image | xargs rpm -ql | grep bzImage | awk -F'/' '{print $3}'", shell=True,stdout=subprocess.PIPE)
        else:
            subp1 = subprocess.Popen("chroot %s rpm -qa | grep kernel-image" % path, shell=True,stdout=subprocess.PIPE)
            rpm_package = subp1.stdout.readline().strip()
            subp=subprocess.Popen("chroot %s rpm -ql %s | grep bzImage | awk -F'/' '{print $3}'" % (path, rpm_package), shell=True,stdout=subprocess.PIPE)

        c=subp.stdout.readline().strip()
        return c

