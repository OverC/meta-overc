SUMMARY = "Essential / minimal initialization service"
DESCRIPTION = "A tool for the initial essential setup"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

RDEPENDS:${PN} = "util-linux bash pflask"

SRC_URI = "file://essential-autostart \
           file://essential-autostart.service \
           file://reload-dom0-snapshot \
           file://reload-dom0-snapshot.service \
           file://daemonize-sigusr1-wait.c \
	   file://essential-opt-mount.service \
	   file://essential-opt-mount \
"

SRC_FILES_LIST = "essential-autostart \
                  reload-dom0-snapshot \
                  essential-opt-mount \
"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "essential-autostart.service reload-dom0-snapshot.service essential-opt-mount.service"

do_compile() {
	${CC} ${CFLAGS} ${LDFLAGS} -Wall ${WORKDIR}/daemonize-sigusr1-wait.c -o ${B}/daemonize-sigusr1-wait
}

do_install() {
    install -d ${D}/${sbindir}
    install -m 0755 ${B}/daemonize-sigusr1-wait ${D}/${sbindir}
    for i in ${SRC_FILES_LIST}; do
        install -m 0755 ${WORKDIR}/${i} ${D}/${sbindir}
    done

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/essential-autostart.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/reload-dom0-snapshot.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/essential-opt-mount.service ${D}${systemd_unitdir}/system/
}

FILES:${PN} += "${sbin} \
                ${systemd_unitdir}/system \
                ${sysconfdir} \
"
