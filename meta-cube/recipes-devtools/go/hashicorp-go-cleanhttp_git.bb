SUMMARY = "Functions for accessing clean Go http.Client values"
HOMEPAGE = "https://github.com/hashicorp/go-cleanhttp"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea"

PKG_NAME = "github.com/hashicorp/go-cleanhttp"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "ad28ea4487f05916463e2423a55166280e8254b5"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_cleanhttp_sysroot_preprocess"

hashicorp_cleanhttp_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
