SUMMARY = "Go clients for various APIs at DataDog."
HOMEPAGE = "https://github.com/DataDog/datadog-go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=229fc88a2f846fc676e9edeafb0e4011"

PKG_NAME = "github.com/DataDog/datadog-go"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "cc2f4770f4d61871e19bfee967bc767fe730b0d9"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "datadog_sysroot_preprocess"

datadog_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
