SUMMARY = "Provides data migration for Consul server nodes"
HOMEPAGE = "https://github.com/hashicorp/consul-migrate"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS += "raft raft-boltdb raft-mdb"

PKG_NAME = "github.com/hashicorp/consul-migrate"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "678fb10cdeae25ab309e99e655148f0bf65f9710"

inherit golang

SYSROOT_PREPROCESS_FUNCS += "consul_migrate_sysroot_preprocess"

consul_migrate_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
    install -d ${SYSROOT_DESTDIR}${prefix}/bin
    cp -a ${D}${prefix}/bin/* ${SYSROOT_DESTDIR}${prefix}/bin/
}

CLEANBROKEN = "1"
