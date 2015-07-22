SUMMARY = "Collection of Open-Source Go libraries and tools"
HOMEPAGE = "https://github.com/hashicorp/go-msgpack"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f4c936a1236aa7f17ca2a0b0ce4bfdd"

PKG_NAME = "github.com/hashicorp/go-msgpack"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "fa3f63826f7c23912c15263591e65d54d080b458"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_msgpack_sysroot_preprocess"

go_msgpack_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
