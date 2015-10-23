SUMMARY = "Golang client for the HashiCorp SCADA service"
HOMEPAGE = "https://github.com/hashicorp/scada-client"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea"

DEPENDS = "net-rpc-msgpackrpc yamux go-metrics"

PKG_NAME = "github.com/hashicorp/scada-client"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "c26580cfe35393f6f4bf1b9ba55e6afe33176bae"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "scada_client_sysroot_preprocess"

scada_client_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
