SUMMARY = "Allow scripts to interact with kernel watchdog service"
HOMEPAGE = "https://github.com/WindRiver-OpenSourceLabs/meta-overc"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://wdt-tool.c;start=4;end=17;md5=f9a38107f4a1d1c971821b50ee2411fc"

SRC_URI = "file://wdt-tool.c \
	file://Makefile \
	"

S = "${WORKDIR}"

do_install() {
	oe_runmake DEST=${D} install
}

