SUMMARY = "OverC support utilities"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRCREV = "${AUTOREV}"
SRC_URI = " \
    git://github.com/OverC/overc-installer.git;branch=master \
    file://source/cube-cmd \
    file://source/cube-ctl \
    file://source/cube-cfg \
    file://source/cube \
    file://source/cube-console \
    file://source/COPYING \
    file://source/cube-device \
    file://source/13-cube-device.rules \
    file://source/cube-device.sh \
    file://source/cube-device-functions \
"

S = "${WORKDIR}/git"

do_install() {
    # TODO: add overc-cctl here, instead of overc-installer package

    install -d ${D}${bindir}
    install -d ${D}${sbindir}

    # cubename comes from overc-installer.git
    install -m755 ${S}/sbin/cubename ${D}${bindir}

    # The rest are local utilities
    install -m755 ${WORKDIR}/source/cube-console ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube-ctl ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube-cmd ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube-cfg ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube ${D}${sbindir}

    # alias "cube" as "c3"
    (
	cd ${D}${sbindir}/
	ln -s cube c3
    )

    # device manamage support
    install -m755 ${WORKDIR}/source/cube-device ${D}${sbindir}
    install -d ${D}${sysconfdir}/udev/scripts
    install -d ${D}${sysconfdir}/udev/rules.d
    install -d ${D}${sysconfdir}/cube-device
    install -m755 ${WORKDIR}/source/cube-device.sh ${D}${sysconfdir}/udev/scripts
    install -m644 ${WORKDIR}/source/13-cube-device.rules ${D}${sysconfdir}/udev/rules.d
    install -m644 ${WORKDIR}/source/cube-device-functions ${D}${sysconfdir}/cube-device
}

PACKAGES =+ "overc-device-utils"

FILES_${PN} += "/opt/${BPN} \
               ${bindir} ${sbindir}"

FILES_overc-device-utils += "${sbindir}/cube-device ${sysconfdir}/udev ${sysconfdir}/cube-device"

RDEPENDS_${PN} += "bash dtach nanoio nanoio-client udev systemd-extra-utils jq"
