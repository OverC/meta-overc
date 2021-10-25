RPROVIDES:${PN}-telnet = "telnet"
RPROVIDES:${PN}-rsh = "rsh"

# bandaid for this error:
# | rmdir: failed to remove 'tmp/work/core2-64-overc-linux/inetutils/1.9.4-r0/image/usr/lib64': No such file or directory
# | WARNING: exit code 1 from a shell command.
do_install:prepend () {
	mkdir -p ${D}/${libdir}
}
