SUMMARY = "A Go (golang) package for representing a list of errors as a single error."
HOMEPAGE = "https://github.com/hashicorp/go-multierror"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d44fdeb607e2d2614db9464dbedd4094"

PKG_NAME = "github.com/hashicorp/go-multierror"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "d30f09973e19c1dfcd120b2d9c4f168e68d6b5d5"

DEPENDS += " hashicorp-errwrap"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    install -m 0644 ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_multierror_sysroot_preprocess"

hashicorp_multierror_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
