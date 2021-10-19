SUMMARY = "Wrap gnu screen into a multi session getty"
HOMEPAGE = "https://github.com/WindRiver-OpenSourceLabs/meta-overc"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "file://COPYING \
	file://screen-getty.c \
	file://screen-gettyrc \
	file://agetty-shell.c \
	file://Makefile \
	file://screen-getty@.service \
	"

S = "${WORKDIR}"

FILES:${PN} += "${systemd_unitdir}"

do_install() {
	oe_runmake DEST=${D} SBIN=${base_sbindir} install
	install -d ${D}/${systemd_unitdir}/system
	install -m 644 ${WORKDIR}/screen-getty@.service ${D}/${systemd_unitdir}/system
}

