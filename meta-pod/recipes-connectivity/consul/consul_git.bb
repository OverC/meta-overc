DESCRIPTION = "A tool for discovering and configuring services in your infrastructure"
HOMEPAGE = "https://www.consul.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS += "golang-cross \
    circbuf \
    consul-migrate \
    go-checkpoint \
    go-msgpack \
    go-syslog \
    hcl \
    logutils \
    memberlist \
    raft \
    raft-boltdb \
    scada-client \
    serf \
    yamux \
    muxado \
    dns \
    cli \
    mapstructure \
    columnize \
    go-radix \
    golang-lru \
    "

PKG_NAME = "github.com/hashicorp/consul"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "5aa90455ce78d4d41578bafc86305e6e6b28d7d2"

S = "${WORKDIR}/git"
CCACHE = ""

#Stops go from installing and testing the package
do_configure(){
}

do_compile() {
    #Setting up GOPATH to find deps (including those already in consul)
    cd ${S}
    rm -rf .gopath
    mkdir -p .gopath/src/$(dirname ${PKG_NAME})
    ln -sf ../../../.. .gopath/src/${PKG_NAME} 
    export GOPATH=${S}:${STAGING_DIR_TARGET}/${prefix}/local/go:${S}/.gopath
    export GOARCH="${TARGET_ARCH}"
    # supported amd64, 386, arm
    if [ "${TARGET_ARCH}" = "x86_64" ]; then
            export GOARCH="amd64"
    fi
    go install github.com/hashicorp/consul
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    install -d ${D}${prefix}/bin
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
    cp -a ${S}/.gopath/bin/* ${D}${prefix}/bin/
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/* ${prefix}/bin/*"
