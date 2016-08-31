FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

NSENTER_PATCH = "${@bb.utils.contains('PV', '2.24.2', 'file://nsenter-to-be-more-flexible-2.42.2.patch', 'file://nsenter-to-be-more-flexible.patch', d)}"

SRC_URI += " \
	file://pam.d \
	file://nsenter-Add-P-option-for-access-to-root-parent-name-.patch \
	${NSENTER_PATCH} \
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
