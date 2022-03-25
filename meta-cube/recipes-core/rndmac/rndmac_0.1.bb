SUMMARY = "Service to allow randomizing the MAC address of an interface"
HOMEPAGE = "https://github.com/WindRiver-OpenSourceLabs/meta-overc"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe"

PKG_NAME = "github.com/miekg/dns"
SRC_URI = "file://LICENSE.GPL2 \
	file://main.c \
	file://Makefile \
	file://_sd-common.h \
	file://sd-id128.h \
	file://siphash24.c \
	file://siphash24.h \
	file://rndmac.service"

S = "${WORKDIR}"

FILES:${PN} += "${systemd_unitdir}/system/rndmac.service"

do_install() {
	oe_runmake DEST=${D}${bindir} install
	install -d ${D}/${systemd_unitdir}/system
	install -m 644 ${WORKDIR}/rndmac.service ${D}/${systemd_unitdir}/system
}

# The hashmac program will compute a mac address for an interface the
# same way that systemd's networkd computes the MAC address so as to
# be completely compatible.  It is intended that this service runs in
# the cube-essential as that own the hardware initially, but it could
# run in the network prime as long as the systemd environment is passed
# down or set by a puppet file.

# This service can be enabled by adding the kernel command line arguments:
#     systemd.wants=rndmac.service systemd.setenv=rndmac=INTERFACE
# Where interace is the interface to be assigned a random static MAC address
# based on the /etc/machine-id file  Example for eth0:
#     systemd.wants=rndmac.service systemd.setenv=rndmac=eth0

