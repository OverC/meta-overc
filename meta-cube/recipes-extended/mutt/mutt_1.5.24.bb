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

SRC_URI[md5sum] = "7f25d27f3c7c82285ac07aac35f5f0f2"
SRC_URI[sha256sum] = "a292ca765ed7b19db4ac495938a3ef808a16193b7d623d65562bb8feb2b42200"

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
