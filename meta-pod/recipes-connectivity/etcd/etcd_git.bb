DESCRIPTION = "A distributed key-value store for shared config and service discovery"
HOMEPAGE = "https://github.com/coreos/etcd"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PKG_NAME = "github.com/coreos/etcd"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "d0f6432b51e37c402450182ce01203dca8a40108"

DEPENDS += "golang-cross"
S = "${WORKDIR}/git"
TARGET_CC_ARCH += "${LDFLAGS}"

do_compile() {
        #Setting up GOPATH to find deps (including those already in etcd)
        cd ${S}
        rm -rf .gopath
        mkdir -p .gopath/src/$(dirname ${PKG_NAME})
        ln -sf ../../../.. .gopath/src/${PKG_NAME}
        export GOPATH=${S}/.gopath
        # supported amd64, 386, arm
        if [ "${TARGET_ARCH}" = "x86_64" ]; then
                export GOARCH="amd64"
        fi
        go install ${PKG_NAME}
}

do_install() {
    install -d ${D}${prefix}/bin
    cp -a ${S}/.gopath/bin/* ${D}${prefix}/bin/
}

FILES_${PN} += "${prefix}/bin/*"
