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
SRCREV = "290ddac99c46371edb721f386771a11cf81bdf0c"
PV = "0.1.0+git${SRCPV}"

DEPENDS += "nanomsg"
RDEPENDS_${PN} += "nanomsg"

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
FILES_${PN}-server = "${sbindir}/nanoserver"
FILES_${PN}-client = "${bindir}/nanoclient"
FILES_${PN} = "\
    ${bindir}/nanoread \
    ${bindir}/nanowrite \
    ${libdir}/libnanoio.so* \
"
