SUMMARY = "ISC Internet Domain Name Server"
HOMEPAGE = "http://www.isc.org/sw/bind/"
SECTION = "console/network"

LICENSE = "ISC & MPL-2.0"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=bf39058a7f64b2a934ce14dc9ec1dd45"

DEPENDS = "openssl libcap zlib"

PACKAGE_FETCH_NAME = "bind"
S = "${WORKDIR}/${PACKAGE_FETCH_NAME}-${PV}"

SRC_URI = "https://ftp.isc.org/isc/bind9/${PV}/${PACKAGE_FETCH_NAME}-${PV}.tar.gz \
           file://conf.patch \
           file://named.service \
           file://bind9 \
           file://generate-rndc-key.sh \
           file://make-etc-initd-bind-stop-work.patch \
           file://init.d-add-support-for-read-only-rootfs.patch \
           file://bind-ensure-searching-for-json-headers-searches-sysr.patch \
           file://0001-configure.in-remove-useless-L-use_openssl-lib.patch \
           file://0001-named-lwresd-V-and-start-log-hide-build-options.patch \
           file://0001-avoid-start-failure-with-bind-user.patch \
           "

SRC_URI[sha256sum] = "afc6d8015006f1cabf699ff19f517bb8fd9c1811e5231f26baf51c3550262ac9"

UPSTREAM_CHECK_URI = "https://ftp.isc.org/isc/bind9/"
# stay at 9.11 until 9.16, from 9.16 follow the ESV versions divisible by 4
UPSTREAM_CHECK_REGEX = "(?P<pver>9.(11|16|20|24|28)(\.\d+)+(-P\d+)*)/"

# BIND >= 9.11.2 need dhcpd >= 4.4.0,
# don't report it here since dhcpd is already recent enough.
CVE_CHECK_IGNORE += "CVE-2019-6470"

inherit autotools
#inherit update-rc.d systemd
inherit useradd pkgconfig multilib_script multilib_header

MULTILIB_SCRIPTS = "${PN}:${bindir}/bind9-config ${PN}:${bindir}/isc-config.sh"

# PACKAGECONFIGs readline and libedit should NOT be set at same time
PACKAGECONFIG ?= "readline"
PACKAGECONFIG[httpstats] = "--with-libxml2=${STAGING_DIR_HOST}${prefix},--without-libxml2,libxml2"
PACKAGECONFIG[readline] = "--with-readline=-lreadline,,readline"
PACKAGECONFIG[libedit] = "--with-readline=-ledit,,libedit"
PACKAGECONFIG[urandom] = "--with-randomdev=/dev/urandom,--with-randomdev=/dev/random,,"
PACKAGECONFIG[python3] = "--with-python=yes --with-python-install-dir=${PYTHON_SITEPACKAGES_DIR} , --without-python, python3-ply-native,"

ENABLE_IPV6 = "--enable-ipv6=${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'yes', 'no', d)}"
EXTRA_OECONF = " ${ENABLE_IPV6} --with-libtool --enable-threads \
                 --disable-devpoll --enable-epoll --with-gost=no \
                 --with-gssapi=no --with-ecdsa=yes --with-eddsa=no \
                 --with-lmdb=no \
                 --sysconfdir=${sysconfdir}/bind \
                 --with-openssl=${STAGING_DIR_HOST}${prefix} \
               "

inherit ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3native setuptools3-base', '', d)}

# dhcp needs .la so keep them
REMOVE_LIBTOOL_LA = "0"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home ${localstatedir}/cache/bind --no-create-home \
                       --user-group bind"

# INITSCRIPT_NAME = "bind"
# INITSCRIPT_PARAMS = "defaults"

# SYSTEMD_SERVICE:${PN} = "named.service"

do_install:prepend() {
	# clean host path in isc-config.sh before the hardlink created
	# by "make install":
	#   bind9-config -> isc-config.sh
	sed -i -e "s,${STAGING_LIBDIR},${libdir}," ${B}/isc-config.sh
}

do_install:append() {

	rmdir "${D}${localstatedir}/run"
	rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"

        # install -d -o bind "${D}${localstatedir}/cache/bind"
	# install -d "${D}${sysconfdir}/bind"
	# install -d "${D}${sysconfdir}/init.d"
	# install -m 644 ${S}/conf/* "${D}${sysconfdir}/bind/"
	# install -m 755 "${S}/init.d" "${D}${sysconfdir}/init.d/bind"
        # if ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'true', 'false', d)}; then
	# 	sed -i -e '1s,#!.*python3,#! /usr/bin/python3,' \
	# 	${D}${sbindir}/dnssec-coverage \
	# 	${D}${sbindir}/dnssec-checkds \
	# 	${D}${sbindir}/dnssec-keymgr
	# fi

	# # Install systemd related files
	# install -d ${D}${sbindir}
	# install -m 755 ${WORKDIR}/generate-rndc-key.sh ${D}${sbindir}
	# install -d ${D}${systemd_unitdir}/system
	# install -m 0644 ${WORKDIR}/named.service ${D}${systemd_unitdir}/system
	# sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
	#        -e 's,@SBINDIR@,${sbindir},g' \
	#        ${D}${systemd_unitdir}/system/named.service

	# install -d ${D}${sysconfdir}/default
	# install -m 0644 ${WORKDIR}/bind9 ${D}${sysconfdir}/default

	# if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
	# 	install -d ${D}${sysconfdir}/tmpfiles.d
	# 	echo "d /run/named 0755 bind bind - -" > ${D}${sysconfdir}/tmpfiles.d/bind.conf
	# fi

	rm -rf "${D}${datadir}"

	oe_multilib_header isc/platform.h
}

CONFFILES_${PN} = " \
	${sysconfdir}/bind/named.conf \
	${sysconfdir}/bind/named.conf.local \
	${sysconfdir}/bind/named.conf.options \
	${sysconfdir}/bind/db.0 \
	${sysconfdir}/bind/db.127 \
	${sysconfdir}/bind/db.empty \
	${sysconfdir}/bind/db.local \
	${sysconfdir}/bind/db.root \
	"

ALTERNATIVE_${PN}-utils = "nslookup"
ALTERNATIVE_LINK_NAME[nslookup] = "${bindir}/nslookup"
ALTERNATIVE_PRIORITY = "100"

PACKAGE_BEFORE_PN += "${PN}-utils"
FILES:${PN}-utils = "${bindir}/host ${bindir}/dig ${bindir}/mdig ${bindir}/nslookup ${bindir}/nsupdate"
FILES:${PN}-dev += "${bindir}/isc-config.h"
FILES:${PN} += "${sbindir}/generate-rndc-key.sh"

PACKAGE_BEFORE_PN += "${PN}-libs"
FILES:${PN}-libs = "${libdir}/*.so*"
FILES:${PN}-staticdev += "${libdir}/*.la"

PACKAGE_BEFORE_PN += "${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3-bind', '', d)}"
FILES:python3-bind = "${sbindir}/dnssec-coverage ${sbindir}/dnssec-checkds \
                ${sbindir}/dnssec-keymgr ${PYTHON_SITEPACKAGES_DIR}"

RDEPENDS:${PN}-dev = ""
RDEPENDS:python3-bind = "python3-core python3-ply"
