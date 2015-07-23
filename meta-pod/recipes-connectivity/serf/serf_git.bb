SUMMARY = "A decentralized solution for service discovery and orchestration"
HOMEPAGE = "https://github.com/hashicorp/serf"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS += "golang-cross \
    cli \
    logutils \
    columnize \
    go-syslog \
    go-msgpack \
    mapstructure \
    go-metrics \
    memberlist \
    circbuf \
    mdns \
    hashicorp-go-net"

PKG_NAME = "github.com/hashicorp/serf"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "5e07170d82d73e20b832e238d71caa531b6d8a2c"

S = "${WORKDIR}/git"

do_compile() {
    #Setting up a symlink to have bolt in a directory go can work with
    cd ${S}
    rm -rf .gopath
    mkdir -p .gopath/src/$(dirname ${PKG_NAME})
    ln -sf ../../../../ .gopath/src/${PKG_NAME}
    #Setting up go variables
    export GOPATH=${S}/.gopath:${STAGING_DIR_TARGET}/${prefix}/local/go
    export GOBIN=${S}/.gopath/bin
    export GOARCH="${TARGET_ARCH}"
    # supported amd64, 386, arm
    if [ "${TARGET_ARCH}" = "x86_64" ]; then
        export GOARCH="amd64"
    fi
    go install ${PKG_NAME}
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "serf_sysroot_preprocess"

serf_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
