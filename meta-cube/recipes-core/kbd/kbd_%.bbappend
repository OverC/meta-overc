FILESPATH_append := ":${@base_set_filespath(['${THISDIR}'], d)}/files"

SRC_URI += "file://chvt.service \
	"
inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "chvt.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

OVERC_ACTIVE_VT ?= "3"

do_install_append() {
    install -d ${D}/lib/systemd/system/
    install -m 0644 ${WORKDIR}/chvt.service ${D}/lib/systemd/system/

    sed -i -e 's,%OVERC_ACTIVE_VT%,${OVERC_ACTIVE_VT},' ${D}/lib/systemd/system/chvt.service
}

FILES_${PN} += "/lib/systemd/system/chvt.service"

