do_install_append() {
	install -d ${D}/${libexecdir}/oci/hooks.d/
	install ${S}/src/import/netns ${D}/${libexecdir}/oci/hooks.d/netns
}

FILES_${PN} += "${libexecdir}/oci/hooks.d/"