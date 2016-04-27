DESCRIPTION = "A tool for discovering and configuring services in your infrastructure"
HOMEPAGE = "https://www.consul.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS += "circbuf \
    consul-migrate \
    go-checkpoint \
    go-msgpack \
    go-metrics \
    go-bindata \
    go-sys \
    go-syslog \
    dockerclient \
    logutils \
    memberlist \
    raft \
    raft-boltdb \
    scada-client \
    serf-go \
    yamux \
    muxado \
    dns \
    cli \
    mapstructure \
    copystructure \
    reflectwalk \
    columnize \
    go-radix \
    golang-lru \
    hashicorp-hil \
    hashicorp-hcl \
    hashicorp-go-cleanhttp \
    hashicorp-go-memdb \
    hashicorp-go-reap \
    hashicorp-go-uuid \
    net-rpc-msgpackrpc \
    "

PKG_NAME = "github.com/hashicorp/consul"
SRC_URI = "git://${PKG_NAME}.git \
           file://consul.service \
           file://0001-prepared_query-make-compatible-with-go1.5.patch \
          "
SRCREV = "f97afda8e15046b41d951bf3b4220372c45df7ab"

CCACHE = ""

inherit systemd golang

ERROR_QA_remove = "ldflags"
WARN_QA_append = " ldflags"

SYSTEMD_SERVICE_${PN} = "consul.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

#Stops go from installing and testing the package
do_configure(){
}

do_install_append() {
    install -d ${D}/${systemd_unitdir}/system
    cp ${WORKDIR}/consul.service ${D}/${systemd_unitdir}/system
}

FILES_${PN} += "${systemd_unitdir}/system"
