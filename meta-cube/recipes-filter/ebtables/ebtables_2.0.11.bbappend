# Work around an issue with linux-libc-headers 4.15
# fixed by mainline kernel commit da360299b673.

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://if_ether.h"

do_compile_prepend() {
    cp ${WORKDIR}/if_ether.h ${S}/include/linux/.
}
