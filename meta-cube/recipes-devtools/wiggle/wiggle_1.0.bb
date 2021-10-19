LICENSE = "GPLv2"
DESCRIPTION = "Wiggle is a program for applying patches that patch cannot apply because of conflicting changes."
SECTION = "console/utils"
PR = "r1"

SRC_URI = "git://github.com/neilbrown/wiggle.git"
SRCREV = "eac35c077388a3dcbbe4ca033c6a43ec6a4fe059"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

DEPENDS = "ncurses groff-native"
RDEPENDS:${PN} = "ncurses ncurses-terminfo"

# pkg makefile has -I. for CFLAGS to get local config.h header
TARGET_CFLAGS =+ "-I${B} -Wimplicit-fallthrough=0"

# custom compile rule to avoid test target which may fail when x-compiling
do_compile () {
	make CFLAGS="${TARGET_CFLAGS}" wiggle wiggle.man
}

do_install () {
	install -D -m 755 wiggle ${D}/${bindir}/wiggle
	install -D -m 644 wiggle.1 ${D}/${mandir}/man1/wiggle.1
}

SRC_URI[md5sum] = "777d8d4c718220063511e82e16275d1b"
SRC_URI[sha256sum] = "44c97b2d47a109c709cdd4181d9ba941fee50dbb64448018b91d4a2fffe69cf2"
