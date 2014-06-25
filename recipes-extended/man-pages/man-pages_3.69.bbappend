# error: file /usr/share/man/man5/passwd.5 from install of shadow-doc-4.1.4.3-r14.corei7_64 conflicts with file from package man-pages-3.68-r0.corei7_64
# error: file /usr/share/man/man3/getspnam.3 from install of shadow-doc-4.1.4.3-r14.corei7_64 conflicts with file from package man-pages-3.68-r0.corei7_64

do_install_append() {
	# FIXME -- make conditional on presence of shadow-doc
	rm -f ${D}/usr/share/man/man5/passwd.5
	rm -f ${D}/usr/share/man/man3/getspnam.3
}
