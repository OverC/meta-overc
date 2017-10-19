import sys, os
import subprocess
import select
import random
import string

ROOTMOUNT = "/essential"
SYSROOT = "/sysroot"
HOSTPID = "/host/proc/1"
FACTORY_SNAPSHOT = ".factory"
CONTAINER_MOUNT = "/var/lib/lxc"

class Utils(object):
    def __init__(self):
        pass

    def _pipe(self, cmd):
        """Runs a command in a subprocess.
        cmd: string Unix command
        Returns (res, stat), the output of the subprocess and the exit status.
        """
        fp = os.popen(cmd)
        res = fp.read()
        stat = fp.close()
        assert stat is None
        return res, stat

    def _compute_checksum(self, filename):
        """Computes the MD5 checksum of the contents of a file.
        filename: string
        """
        cmd = 'md5sum ' + filename
        return self._pipe(cmd)[0].split()[0].strip()

    def _nsenter(self, pid, args):
        cmd = []
        cmd.append("nsenter -t %s" % pid)
        cmd.append("-n -m -i --")
        cmd.append(args)
        cmd_s = ' '.join(cmd)
        process = Process()
        retval = process.run(cmd_s)
        self.message = process.message
        return retval

    def _random_str(self, size=6, chars=string.ascii_uppercase + string.digits):
        return ''.join(random.choice(chars) for _ in range(size))
        
class Process(object):
    def __init__(self):
        self.stdout = ''
        self.stderr = ''
        self.message = ''
        self.retval = 0

    def run(self, cmd):
        print("Running: %s" % cmd)

        child = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        while True:
            fds = select.select([child.stdout.fileno(), child.stderr.fileno()], [], [])

            for fd in fds[0]:
                if fd == child.stdout.fileno():
                    read = child.stdout.readline()
                    if read != '':
                        sys.stdout.write(read)
                    self.stdout += read
                    self.message += read
                if fd == child.stderr.fileno():
                    read = child.stderr.readline()
                    if read != '':
                        sys.stderr.write(read)
                    self.stderr += read
                    self.message += read
            if child.poll() != None:
                break
        self.retval = child.poll()
        return self.retval
