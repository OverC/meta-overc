SUMMARY = "Errwrap is a Go (golang) library for wrapping and querying errors."
HOMEPAGE = "https://github.com/hashicorp/errwrap"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

PKG_NAME = "github.com/hashicorp/errwrap"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "7554cd9344cec97297fa6649b055a8c98c2a1e55"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_errwrap_sysroot_preprocess"

hashicorp_errwrap_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
