do_install:append() {
	install -d ${D}/${libexecdir}/oci/hooks.d/
	install ${S}/src/import/netns ${D}/${libexecdir}/oci/hooks.d/netns
}

FILES:${PN} += "${libexecdir}/oci/hooks.d/"