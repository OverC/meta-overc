DESCRIPTION = "A distributed key-value store for shared config and service discovery"
HOMEPAGE = "https://github.com/coreos/etcd"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PKG_NAME = "github.com/coreos/etcd"
SRC_URI = "git://${PKG_NAME}.git \
    file://scripts-all-scripts-should-use-same-path-for-bash.patch \
    "

SRCREV = "d0f6432b51e37c402450182ce01203dca8a40108"

TARGET_CC_ARCH += "${LDFLAGS}"

inherit golang

#During packaging etcd gets the warning "no GNU hash in elf binary"
#This issue occurs due to compiling without ldflags, but a
#solution has yet to be found. For now we ignore this error with
#the line below.
INSANE_SKIP_${PN} = "ldflags"

RDEPENDS_${PN} = "bash"
