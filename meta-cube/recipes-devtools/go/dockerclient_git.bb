SUMMARY = "Go client for the Docker remote API."
HOMEPAGE = "https://github.com/fsouza/go-dockerclient"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd"

PKG_NAME = "github.com/fsouza/go-dockerclient"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "7e2450a717e8725de58dc1530218cd64117861b3"

S = "${WORKDIR}/git"

do_compile() {
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "dockerclient_sysroot_preprocess"

dockerclient_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
