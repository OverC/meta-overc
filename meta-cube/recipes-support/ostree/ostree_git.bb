SUMMARY = "Tool for managing bootable, immutable, versioned filesystem trees"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

inherit autotools-brokensep pkgconfig systemd gobject-introspection

INHERIT_remove_class-native = "systemd"

SRC_URI = "gitsm://github.com/ostreedev/ostree.git;branch=master \
           file://system-export.sh \
	   file://0001-ostree-fix-the-issue-of-cannot-get-the-config-entrie.patch \
	   file://test.patch \
           file://sample.conf \
	   file://tmp_fix.patch \
           file://0001-ostree-secure-boot-support-for-no-change-to-grub.cfg.patch \
           file://0001-Allow-updating-files-in-the-boot-directory.patch  \
           file://0002-u-boot-add-bootdir-to-the-generated-uEnv.txt.patch \
           file://0003-uboot-add-non-default-for-bootdirs-to-uEnv.txt.patch \
           file://ostree_swap_bootentry_atomically.patch \
	   file://using-bash-specifically.patch \
	   file://0001-create-boot-symlink-based-on-relative-path.patch \
	   file://0001-retrieve-correct-boot-prefix-at-runtime.patch \
	   file://0001-encrypt-decrypt-password-of-remote-repository-uri.patch \
	   file://0001-tweak-regex-of-ostree-system-generator-for-pulsar.patch \
	   file://0001-deploy-using-etc-in-runtime-as-merge-source.patch \
	"


SRCREV = "414891865568ee95978bfe2091ef6f8416726a1f"

CLEANBROKEN = "1"

PV = "2018.7+git${SRCPV}"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"

DEPENDS += "attr libarchive glib-2.0 pkgconfig gpgme fuse libsoup-2.4 e2fsprogs gtk-doc-native curl bison-native"
DEPENDS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd', '', d)}"
DEPENDS_remove_class-native = "systemd-native"

RDEPENDS_${PN} = "python util-linux-libuuid util-linux-libblkid util-linux-libmount libcap xz bash openssl"

RDEPENDS_${PN}_remove_class-native = "python-native"

RDEPENDS_${PN}_append_class-target = " pv"

RDEPENDS_${PN}_remove_class-nativesdk = "util-linux-libuuid util-linux-libblkid util-linux-libmount"
RDEPENDS_${PN}_append_class-nativesdk = " util-linux "

EXTRA_OECONF = "--with-libarchive --disable-gtk-doc --disable-gtk-doc-html --disable-gtk-doc-pdf --disable-man --with-smack --with-builtin-grub2-mkconfig  \
 --libdir=${libdir} "
EXTRA_OECONF_append_class-native = " --enable-wrpseudo-compat"

# Path to ${prefix}/lib/ostree/ostree-grub-generator is hardcoded on the
#  do_configure stage so we do depend on it
SYSROOT_DIR = "${STAGING_DIR_TARGET}"
SYSROOT_DIR_class-native = "${STAGING_DIR_NATIVE}"
do_configure[vardeps] += "SYSROOT_DIR"

SYSTEMD_REQUIRED = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}"
SYSTEMD_REQUIRED_class-native = ""

SYSTEMD_SERVICE_${PN} = "ostree-prepare-root.service ostree-remount.service"
SYSTEMD_SERVICE_${PN}_class-native = ""

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
PACKAGECONFIG_class-native = ""
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/ --with-dracut"

FILES_${PN} += "${libdir}/ostree/ ${libdir}/ostbuild"

export STAGING_INCDIR
export STAGING_LIBDIR

do_configure() {
 unset docdir
 NOCONFIGURE=1 ./autogen.sh
 oe_runconf
}

do_compile_prepend() {
 export BUILD_SYS="${BUILD_SYS}"
 export HOST_SYS="${HOST_SYS}"
}

export SYSTEMD_REQUIRED

do_install_append() {
 if [ -n ${SYSTEMD_REQUIRED} ]; then
  install -p -D ${S}/src/boot/ostree-prepare-root.service ${D}${systemd_unitdir}/system/ostree-prepare-root.service
  install -p -D ${S}/src/boot/ostree-remount.service ${D}${systemd_unitdir}/system/ostree-remount.service
 fi
 install -d ${D}/${sysconfdir}/ostree/remotes.d/
 install  ${WORKDIR}/sample.conf ${D}/${sysconfdir}/ostree/remotes.d/
 install -m 0755 ${WORKDIR}/system-export.sh ${D}/${bindir}/system-export
}

do_install_append_class-native() {
	create_wrapper ${D}${bindir}/ostree OSTREE_GRUB2_EXEC="${STAGING_LIBDIR_NATIVE}/ostree/ostree-grub-generator"
}


FILES_${PN} += " \
    ${@'${systemd_unitdir}/system/' if d.getVar('SYSTEMD_REQUIRED', True) else ''} \
    ${@'/usr/lib/dracut/modules.d/98ostree/module-setup.sh' if d.getVar('SYSTEMD_REQUIRED', True) else ''} \
    ${datadir}/gir-1.0 \
    ${datadir}/gir-1.0/OSTree-1.0.gir \
    ${datadir}/bash-completion \
    /usr/lib/girepository-1.0 \
    /usr/lib/girepository-1.0/OSTree-1.0.typelib \
    /usr/lib/ostree/ostree-grub-generator \
    /usr/lib/ostree/ostree-remount \
    ${systemd_unitdir} \
    /usr/lib/tmpfiles.d \
"

PACKAGES =+ "${PN}-switchroot"

FILES_${PN}-switchroot = "/usr/lib/ostree/ostree-prepare-root"
RDEPENDS_${PN}-switchroot = ""
DEPENDS_remove_class-native = "systemd-native"

