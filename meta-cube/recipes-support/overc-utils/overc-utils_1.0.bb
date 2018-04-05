SUMMARY = "OverC support utilities"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/source/COPYING;md5=751419260aa954499f7abaabaa882bbe"

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI = " \
    file://source/cube-cmd \
    file://source/c3-cap \
    file://source/cube-ctl \
    file://source/cube-cfg \
    file://source/nctl \
    file://source/cube \
    file://source/cube-console \
    file://source/COPYING \
    file://source/cube-device \
    file://source/13-cube-device.rules \
    file://source/cube-device.sh \
    file://source/cube-device-functions \
    file://source/c3-completions \
    file://source/c3-construct \
    file://source/c3-cmds/* \
"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${sbindir}

    # The rest are local utilities
    install -m755 ${WORKDIR}/source/cube-console ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube-ctl ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube-cmd ${D}${sbindir}
    install -m755 ${WORKDIR}/source/c3-cap ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube-cfg ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube ${D}${sbindir}
    install -m755 ${WORKDIR}/source/nctl ${D}${sbindir}

    install -d ${D}${sbindir}/c3-cmds
    install -m755 ${WORKDIR}/source/c3-cmds/* ${D}${sbindir}/c3-cmds
    install -m755 ${WORKDIR}/source/c3-construct ${D}${sbindir}

    # alias "cube" as "c3"
    (
	cd ${D}${sbindir}/
	ln -sf cube c3
	ln -sf cube-cfg c3-cfg
	ln -sf cube-cmd c3-cmd
	ln -sf cube-ctl c3-ctl
	ln -sf cube-console c3-console
    )

    # device manamage support
    install -m755 ${WORKDIR}/source/cube-device ${D}${sbindir}
    install -d ${D}${sysconfdir}/udev/scripts
    install -d ${D}${sysconfdir}/udev/rules.d
    install -d ${D}${sysconfdir}/cube-device
    install -m755 ${WORKDIR}/source/cube-device.sh ${D}${sysconfdir}/udev/scripts
    install -m644 ${WORKDIR}/source/13-cube-device.rules ${D}${sysconfdir}/udev/rules.d
    install -m644 ${WORKDIR}/source/cube-device-functions ${D}${sysconfdir}/cube-device

    install -d ${D}${datadir}/bash-completion
    install -d ${D}${datadir}/bash-completion/completions
    install -m644 ${WORKDIR}/source/c3-completions ${D}${datadir}/bash-completion/completions/c3
}

PACKAGES =+ "overc-device-utils"

FILES_${PN} += "/opt/${BPN} ${datadir}/bash-completion \
               ${bindir} ${sbindir} ${localstatedir}/lib/cube-cmd-server/"

FILES_overc-device-utils += "${sbindir}/cube-device ${sysconfdir}/udev ${sysconfdir}/cube-device"

RDEPENDS_${PN} += "bash dtach nanomsg udev systemd-extra-utils jq overc-installer udocker \
                   sed which grep \
"
