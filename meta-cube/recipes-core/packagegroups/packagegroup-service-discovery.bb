SUMMARY = "Packages needed for a service discovery and resource management daemon"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

inherit packagegroup

PACKAGES = "\
    packagegroup-service-discovery \
    packagegroup-service-discovery-and-configuration \
"

RDEPENDS_packagegroup-service-discovery = "\
    etcd  \
    skopeo \
    umoci \
"

RDEPENDS_packagegroup-service-discovery-and-configuration = "\
    consul \
"


