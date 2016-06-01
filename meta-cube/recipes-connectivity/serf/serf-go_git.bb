SUMMARY = "A decentralized solution for service discovery and orchestration"
HOMEPAGE = "https://github.com/hashicorp/serf"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS += "go-cross \
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
SRCREV = "44945ab711fb48f1cee51d248568487b938c178d"

S = "${WORKDIR}/git"

#Due to an import conflict between serf and newer versions of go.net, the
#compilation of the serf binary has been removed from this recipe.

do_compile() {
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/

    # we don't need the Debian/Ubuntu package metadata
    rm -rf ${D}${prefix}/local/go/src/${PKG_NAME}/ops-misc/debian/
}

SYSROOT_PREPROCESS_FUNCS += "serf_go_sysroot_preprocess"

serf_go_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"

RDEPENDS_${PN} = "bash"
