SUMMARY = "Go client for the Docker remote API."
HOMEPAGE = "https://github.com/fsouza/go-dockerclient"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=9687fb4c7ee49afa7b6afd459d1f7169"

PKG_NAME = "github.com/fsouza/go-dockerclient"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "7e2450a717e8725de58dc1530218cd64117861b3"
GO_IMPORT = "import"

inherit go

S = "${WORKDIR}/git"

do_compile() {
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "dockerclient_sysroot_preprocess"

dockerclient_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
RDEPENDS_${PN} = "bash"
