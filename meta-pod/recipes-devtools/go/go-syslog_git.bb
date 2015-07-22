SUMMARY = "Provides gsyslog without introducing cross-compilation issues"
HOMEPAGE = "https://github.com/hashicorp/go-syslog"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb04212e101fbbd028f325e04ad45778"

PKG_NAME = "github.com/hashicorp/go-syslog"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "42a2b573b664dbf281bd48c3cc12c086b17a39ba"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_syslog_sysroot_preprocess"

go_syslog_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
