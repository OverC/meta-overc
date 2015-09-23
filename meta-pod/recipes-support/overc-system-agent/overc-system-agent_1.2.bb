SUMMARY = "OverC host update system"
SECTION = "devel/python"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = " \
    file://${BPN}-${PV}/* \
"

inherit distutils

RDEPENDS_${PN} = "\
	btrfs-tools \
	python-smartpm \
	python-argparse \
	python-subprocess \
	python-flask \
	python-itsdangerous \
	python-jinja2 \
	python-markupsafe \
	python-werkzeug \
	bash \
	overc-installer \
	"

FILES_${PN}-dev += "${libdir}/pkgconfig"

do_install() {
	install -d ${D}/opt/${BPN}/
	install -d ${D}/opt/${BPN}/Overc
	install -d ${D}/opt/${BPN}/Overc/backends
	install -d ${D}/opt/${BPN}/container-scripts

	install -m755 ${S}/container-scripts/* ${D}/opt/${BPN}/container-scripts

	install -m644 ${S}/Overc/*.py ${D}/opt/${BPN}/Overc/
	install -m644 ${S}/Overc/backends/*.py ${D}/opt/${BPN}/Overc/backends

	install -m644 ${S}/COPYING ${D}/opt/${BPN}/
	install -m644 ${S}/PKG-INFO ${D}/opt/${BPN}/
	install -m644 ${S}/README* ${D}/opt/${BPN}/
	install -m755 ${S}/overc ${D}/opt/${BPN}/
	install -m755 ${S}/*.py ${D}/opt/${BPN}/

	install -d ${D}/${sysconfdir}/overc/container/
	install -m755 ${S}/container-scripts/* ${D}/${sysconfdir}/overc/container/
}

FILES_${PN} += "/opt/${BPN} ${sysconfdir}/overc"
