SUMMARY = "Implements a radix tree in go"
HOMEPAGE = "https://github.com/armon/go-radix"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb04212e101fbbd028f325e04ad45778"

PKG_NAME = "github.com/armon/go-radix"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "fbd82e84e2b13651f3abc5ffd26b65ba71bc8f93"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_radix_sysroot_preprocess"

go_radix_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
