FILESPATH_append := ":${@base_set_filespath(['${THISDIR}'], d)}/files"

SRC_URI += "file://chvt.service \
	"
inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "chvt.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

do_install_append() {
    install -d ${D}/lib/systemd/system/
    install -m 0644 ${WORKDIR}/chvt.service ${D}/lib/systemd/system/
}

FILES_${PN} += "/lib/systemd/system/chvt.service"

