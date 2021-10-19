SUMMARY = "This project provides with the utilities and library \
basing on nanomsg"
DESCRIPTION = " \
This project provides with the utilities and library basing on \
nanomsg. Currently, the resulting binaries consist of nanoread, \
nanowrite, nanoclient, nanoserver and libnanoio.so. \
"
SECTION = "devel"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=3c03275605209651d6b99457f0b2778e"

SRC_URI = " \
    git://github.com/WindRiver-OpenSourceLabs/nanoio.git \
"
SRCREV = "cad6ed022d7aa8e0909d50277cba0398b915ae46"
PV = "0.1.0+git${SRCPV}"

DEPENDS += "nanomsg"
RDEPENDS:${PN} += "nanomsg"

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

EXTRA_OEMAKE = " \
    prefix="${prefix}" \
    bindir="${bindir}" \
    sbindir="${sbindir}" \
    libdir="${libdir}" \
    includedir="${includedir}" \
    nanomsg_libdir="${STAGING_LIBDIR}" \
    nanomsg_includedir="${STAGING_INCDIR}" \
    CC="${CC}" \
    EXTRA_CFLAGS="${CFLAGS}" \
    EXTRA_LDFLAGS="${LDFLAGS}" \
"

do_install() {
    oe_runmake install DESTDIR="${D}"
}

PACKAGES += "${PN}-server ${PN}-client"
FILES:${PN}-server = "${sbindir}/nanoserver"
FILES:${PN}-client = "${bindir}/nanoclient"
FILES:${PN} = "\
    ${bindir}/nanoread \
    ${bindir}/nanowrite \
    ${libdir}/libnanoio.so* \
"
