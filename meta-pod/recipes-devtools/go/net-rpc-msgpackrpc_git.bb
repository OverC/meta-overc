SUMMARY = "Provides functions of jsonrpc by communicating with MessagePack"
HOMEPAGE = "https://github.com/hashicorp/net-rpc-msgpackrpc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=fb70e2b4f339ce6b09fffba9a882dc53"

DEPENDS = "go-msgpack"

PKG_NAME = "github.com/hashicorp/net-rpc-msgpackrpc"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "d377902b7aba83dd3895837b902f6cf3f71edcb2"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "net_rpc_msgpackrpc_sysroot_preprocess"

net_rpc_msgpackrpc_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
