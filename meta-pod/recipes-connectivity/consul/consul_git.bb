DESCRIPTION = "A tool for discovering and configuring services in your infrastructure"
HOMEPAGE = "https://www.consul.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS += "circbuf \
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
    serf-go \
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
SRC_URI = "git://${PKG_NAME}.git \
           file://consul.service \
          "
SRCREV = "5aa90455ce78d4d41578bafc86305e6e6b28d7d2"

CCACHE = ""

inherit systemd golang

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
