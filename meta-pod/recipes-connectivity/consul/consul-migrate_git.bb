SUMMARY = "Provides data migration for Consul server nodes"
HOMEPAGE = "https://github.com/hashicorp/consul-migrate"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS += "raft raft-boltdb raft-mdb"

PKG_NAME = "github.com/hashicorp/consul-migrate"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "678fb10cdeae25ab309e99e655148f0bf65f9710"

S = "${WORKDIR}/git"

do_compile() {
    #Setting up a symlink to have bolt in a directory go can work with
    cd ${S}
    rm -rf .gopath
    mkdir -p .gopath/src/$(dirname ${PKG_NAME})
    ln -sf ../../../../ .gopath/src/${PKG_NAME}
    #Setting up go variables
    export GOPATH="${S}/.gopath:${STAGING_DIR_TARGET}/${prefix}/local/go"
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
    install -d ${D}${prefix}/bin
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
    cp -a ${S}/.gopath/bin/* ${D}${prefix}/bin/
}

SYSROOT_PREPROCESS_FUNCS += "consul_migrate_sysroot_preprocess"

consul_migrate_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
    install -d ${SYSROOT_DESTDIR}${prefix}/bin
    cp -a ${D}${prefix}/bin/* ${SYSROOT_DESTDIR}${prefix}/bin/
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/* ${prefix}/bin/*"
