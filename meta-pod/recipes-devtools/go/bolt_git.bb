SUMMARY = "Go package providing a key/value store"
HOMEPAGE = "https://github.com/boltdb/bolt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13b2a308eefa10d841e3bf2467dbe07a"

DEPENDS += "golang-cross"

PKG_NAME = "github.com/boltdb/bolt"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "be4e606bc48012421bc90eda58a8aad7a95aaf83"

S = "${WORKDIR}/git"

do_compile() {
    #Setting up a symlink to have bolt in a directory go can work with
    cd ${S}
    rm -rf .gopath
    mkdir -p .gopath/src/$(dirname ${PKG_NAME})
    ln -sf ../../../../ .gopath/src/${PKG_NAME}
    #Setting up go variables
    export GOPATH=${S}/.gopath
    export GOBIN=${S}/.gopath/bin
    export GOARCH="${TARGET_ARCH}"
    # supported amd64, 386, arm
    if [ "${TARGET_ARCH}" = "x86_64" ]; then
        export GOARCH="amd64"
    fi
    go install github.com/boltdb/bolt/cmd/bolt
    ls $GOPATH
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    install -d ${D}${prefix}/bin
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
    cp -a ${S}/.gopath/bin/* ${D}${prefix}/bin/
}

SYSROOT_PREPROCESS_FUNCS += "bolt_sysroot_preprocess"

bolt_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
    install -d ${SYSROOT_DESTDIR}${prefix}/bin
    cp -a ${D}${prefix}/bin/* ${SYSROOT_DESTDIR}${prefix}/bin/
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/* ${prefix}/bin/*"
