import sys, os

ROOTMOUNT = "/essential"

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

