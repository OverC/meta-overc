SUMMARY = "Emulation of detach feature of screen"

DESCRIPTION = "dtach is a program written in C that emulates the \
detach feature of screen, which allows a program to be executed in an \
environment that is protected from the controlling terminal."

HOMEPAGE = "https://github.com/crigler/dtach"

SECTION = "console/utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/crigler/dtach.git \
	file://Add-additional-functions-to-dtach.patch \
	file://Add-quit-on-detach-and-squash-r-options.patch \
	file://Add-ability-to-run-a-client-with-from-a-non-interact.patch \
	file://Handle-EOF-properly-on-client-without-a-terminal.patch \
	file://0001-Add-console-socket-option.patch \
	"
SRCREV = "7acac922770597f5da5df7b290078770d20dac32"

inherit autotools

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 dtach ${D}${bindir}
	install -d ${D}${mandir}/man1
	install -m 0644 ${S}/dtach.1 ${D}${mandir}/man1
}
