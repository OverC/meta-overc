SUMMARY = "Configuration language for use with command-line tools"
HOMEPAGE = "https://github.com/hashicorp/hcl"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS += "go-multierror"

PKG_NAME = "github.com/hashicorp/hcl"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "513e04c400ee2e81e97f5e011c08fb42c6f69b84"

S = "${WORKDIR}/git"

#Empty do_compile to stop go from trying to update source and test it
do_compile() {
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hcl_sysroot_preprocess"

hcl_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"

