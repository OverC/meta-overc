SUMMARY = "Go (golang) library for deep copying values in Go."
HOMEPAGE = "https://github.com/mitchellh/copystructure"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56da355a12d4821cda57b8f23ec34bc4"

PKG_NAME = "github.com/mitchellh/copystructure"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "80adcec1955ee4e97af357c30dee61aadcc02c10"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "copystructure_sysroot_preprocess"

copystructure_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
