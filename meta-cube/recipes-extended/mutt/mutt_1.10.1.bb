SECTION = "console/network"

DESCRIPTION = "Mutt is a small but very powerful text-based \
MIME mail client. It is highly configurable, and is well-suited \
to the mail power user with advanced features like key \
bindings, keyboard macros, mail threading, regular expression \
searches, and a powerful pattern matching language for selecting \
groups of messages."

LICENSE = "GPLv2"

DEPENDS = "ncurses gnutls gpgme mime-support"
RDEPENDS_${PN} = "perl"

SRC_URI = "https://bitbucket.org/mutt/mutt/downloads/mutt-${PV}.tar.gz \
           file://docs-use-sysroot-for-CPP_FLAGS.patch \
           file://libs-teach-gpgme-about-pkg-config.patch \
           file://makedoc.patch"

LIC_FILES_CHKSUM = "file://GPL;md5=ebf4e8b49780ab187d51bd26aaa022c6"

SRC_URI[md5sum] = "f1564f81ed5f8bacb7e041edc71d5347"
SRC_URI[sha256sum] = "734a3883158ec3d180cf6538d8bd7f685ce641d2cdef657aa0038f76e79a54a0"

S = "${WORKDIR}/${PN}-${PV}"

inherit autotools-brokensep

EXTRA_OECONF = "--with-curses=${STAGING_LIBDIR}/.. \
	        --enable-pop \
                --enable-imap \
                --with-gnutls \
                --with-mailpath=${localstatedir}/spool/mail\
                "

# TODO: --enable-gpgme: needs m4 macro converted to pkg-config
               
do_compile_prepend () {
         ${BUILD_CC} doc/makedoc.c -o doc/makedoc
}

# mime.types conflicts with the one from mime_support
do_install_append () {
	rm -f ${D}${sysconfdir}/mime.types
}
