# hack around broken multilib by introducing a wrapper that
# will call the lib32-gcc binaries directly when requested.

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI += "file://gcc-wrapper.sh"

do_install_append() {
	rm -f ${D}${bindir}/gcc
	install -m 755 ${SW}/gcc-wrapper.sh ${D}${bindir}/gcc
}

