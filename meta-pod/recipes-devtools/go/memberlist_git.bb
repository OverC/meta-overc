SUMMARY = "Go library that manages cluster membership and member failure detection"
HOMEPAGE = "https://github.com/hashicorp/memberlist"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS = "go-metrics go-msgpack"

PKG_NAME = "github.com/hashicorp/memberlist"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "c0dd21a51b7f6af4ecb9e1c954bc83b2aa0fd2d8"

S = "${WORKDIR}/git"

#Empty do_compile to stop go from testing the package
do_compile() {
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "memberlist_sysroot_preprocess"

memberlist_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
