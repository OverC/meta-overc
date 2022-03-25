SUMMARY = "OverC host update system"
SECTION = "devel/python"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = " \
    file://${BPN}-${PV} \
"
inherit distutils3 systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "factory-reset.service"

PACKAGES += "${PN}-bash-completion"

RDEPENDS:${PN}-bash-completion = "bash-completion"

RDEPENDS:${PN} = "\
	btrfs-tools \
	python3-flask \
	python3-itsdangerous \
	python3-jinja2 \
	python3-markupsafe \
	python3-werkzeug \
	python3-core \
	bash \
	bc \
	overc-installer \
	${PN}-bash-completion \
	"

FILES:${PN}-dev += "${libdir}/pkgconfig"
FILES:${PN}-bash-completion = "${datadir}/bash-completion/*"

do_install() {
	install -d ${D}/opt/${BPN}/
	install -d ${D}/opt/${BPN}/Overc
	install -d ${D}/opt/${BPN}/Overc/backends
	install -d ${D}/opt/${BPN}/container-scripts
	install -d ${D}/opt/${BPN}/test

	install -m755 ${S}/container-scripts/* ${D}/opt/${BPN}/container-scripts

	install -m644 ${S}/Overc/*.py ${D}/opt/${BPN}/Overc/
	install -m644 ${S}/Overc/backends/*.py ${D}/opt/${BPN}/Overc/backends

	install -m644 ${S}/COPYING ${D}/opt/${BPN}/
	install -m644 ${S}/PKG-INFO ${D}/opt/${BPN}/
	install -m644 ${S}/README* ${D}/opt/${BPN}/
	install -m755 ${S}/overc ${D}/opt/${BPN}/
	install -m755 ${S}/*.py ${D}/opt/${BPN}/

	install -m755 ${S}/test/*.sh ${D}/opt/${BPN}/test

	install -d ${D}/${sysconfdir}/overc/container/
	install -m755 ${S}/container-scripts/* ${D}/${sysconfdir}/overc/container/

        # systemd services
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/factory-reset.service ${D}${systemd_unitdir}/system/

        # Install bash-completion script
        install -d ${D}/${datadir}/bash-completion/completions/
        install -m 644 ${S}/bash-completion/* ${D}/${datadir}/bash-completion/completions/overc
}

FILES:${PN} += "/opt/${BPN} ${sysconfdir}/overc \
                ${base_libdir}/systemd \
	      "

