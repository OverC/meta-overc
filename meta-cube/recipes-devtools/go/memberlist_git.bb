SUMMARY = "Go library that manages cluster membership and member failure detection"
HOMEPAGE = "https://github.com/hashicorp/memberlist"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS = "go-metrics go-msgpack go-multierror"
RDEPENDS_${PN} = "bash"

PKG_NAME = "github.com/hashicorp/memberlist"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "88ac4de0d1a0ca6def284b571342db3b777a4c37"

S = "${WORKDIR}/git"

#Empty do_compile to stop go from testing the package
do_compile() {
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "memberlist_sysroot_preprocess"

memberlist_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"

