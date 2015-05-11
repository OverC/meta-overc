SECTION = "console/network"

DESCRIPTION = "Mutt is a small but very powerful text-based \
MIME mail client. It is highly configurable, and is well-suited \
to the mail power user with advanced features like key \
bindings, keyboard macros, mail threading, regular expression \
searches, and a powerful pattern matching language for selecting \
groups of messages."

LICENSE = "GPLv2"

DEPENDS = "ncurses gnutls gpgme"
RDEPENDS_${PN} = "perl"

SRC_URI = "https://bitbucket.org/mutt/mutt/downloads/mutt-${PV}.tar.gz \
           file://docs-use-sysroot-for-CPP_FLAGS.patch \
           file://libs-teach-gpgme-about-pkg-config.patch \
           file://makedoc.patch"

LIC_FILES_CHKSUM = "file://GPL;md5=ebf4e8b49780ab187d51bd26aaa022c6"

SRC_URI[md5sum] = "11f5b6a3eeba1afa1257fe93c9f26bff"
SRC_URI[sha256sum] = "3af0701e57b9e1880ed3a0dee34498a228939e854a16cdccd24e5e502626fd37"

S = "${WORKDIR}/${PN}-${PV}"

inherit autotools-brokensep

EXTRA_OECONF = "--with-curses=${STAGING_LIBDIR}/.. \
	        --enable-pop \
                --enable-imap \
                --with-gnutls"

# TODO: --enable-gpgme: needs m4 macro converted to pkg-config
               
do_compile_prepend () {
         ${BUILD_CC} doc/makedoc.c -o doc/makedoc
}
