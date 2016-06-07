SUMMARY = "HCL (HashiCorp Configuration Language) is a configuration language built by HashiCorp"
HOMEPAGE = "https://github.com/hashicorp/hcl"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

PKG_NAME = "github.com/hashicorp/hcl"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "27a57f2605e04995c111273c263d51cee60d9bc4"

S = "${WORKDIR}/git"

inherit golang

do_compile() {
    oe_runmake generate
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_hcl_sysroot_preprocess"

hashicorp_hcl_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"
