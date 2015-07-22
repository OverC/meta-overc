SUMMARY = "Go package for representing a list of errors as a single error"
HOMEPAGE = "https://github.com/hashicorp/go-multierror"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d44fdeb607e2d2614db9464dbedd4094"

PKG_NAME = "github.com/hashicorp/go-multierror"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "56912fb08d85084aa318edcf2bba735b97cf35c5"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_multierror_sysroot_preprocess"

go_multierror_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
