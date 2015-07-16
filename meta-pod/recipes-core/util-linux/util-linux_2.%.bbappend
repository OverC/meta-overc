FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	file://pam.d \
	file://nsenter-to-be-more-flexible.patch \
"

FILES_${PN} += "${sysconfdir}/pam.d/"

do_install_append() {
	install -d ${D}${sysconfdir}
	install -d ${D}${sysconfdir}/pam.d
	for f in ${WORKDIR}/pam.d/*
	do
		install -m 644 $f ${D}${sysconfdir}/pam.d
	done
}
