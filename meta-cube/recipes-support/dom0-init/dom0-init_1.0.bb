SUMMARY = "dom0 / minimal initialization service"
DESCRIPTION = "A tool for the initial dom0 setup"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

RDEPENDS_${PN} = "overc-utils util-linux bash"

SRC_URI = "file://dom0-containers \
           file://dom0-contctl.service \
           file://dom0-contctl.conf \
"

SRC_FILES_LIST="dom0-containers \
"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "dom0-contctl.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

systemd_postinst() {
OPTS=""

if [ -n "$D" ]; then
    OPTS="--root=$D"
fi

if type systemctl >/dev/null 2>/dev/null; then
	systemctl $OPTS ${SYSTEMD_AUTO_ENABLE} ${SYSTEMD_SERVICE}
fi
}


do_install() {
    install -d ${D}/${sbindir}
    for i in ${SRC_FILES_LIST}; do
        install -m 0755 ${WORKDIR}/${i} ${D}/${sbindir}
    done

    install -d ${D}/lib/systemd/system/
    install -m 0644 ${WORKDIR}/dom0-contctl.service ${D}/lib/systemd/system/

    install -d ${D}/${sysconfdir}
    install -m 0744 ${WORKDIR}/dom0-contctl.conf ${D}/${sysconfdir}
}

FILES_${PN} += "${sbin} \
                /lib/systemd/system \
                ${sysconfdir} \
"
