SUMMARY = "HCL (HashiCorp Configuration Language) is a configuration language built by HashiCorp"
HOMEPAGE = "https://github.com/hashicorp/hcl"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

PKG_NAME = "github.com/hashicorp/hcl"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "27a57f2605e04995c111273c263d51cee60d9bc4"

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake generate
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_hcl_sysroot_preprocess"

hashicorp_hcl_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
