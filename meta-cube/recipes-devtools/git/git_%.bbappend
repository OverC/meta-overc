do_install_append () {
	rm -rf ${D}/${libdir}/site_perl/
	# when this is fixed in master, this rmdir should fail.
	rmdir ${D}/${libdir}
}
