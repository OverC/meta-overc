SUMMARY = "Packages needed for a service discovery and resource management daemon"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-service-discovery \
    packagegroup-service-discovery-and-configuration \
"

RDEPENDS:packagegroup-service-discovery = "\
    etcd  \
    skopeo \
    umoci \
"

RDEPENDS:packagegroup-service-discovery-and-configuration = "\
    consul \
"


