DESCRIPTION = "Tools for making and managing initramfs images"
LICENSE = "GPLv2"

SRC_URI = "http://http.debian.net/debian/pool/main/i/initramfs-tools/initramfs-tools_0.120.tar.xz"

SRC_URI[md5sum] = "d4b260cc2244b44ba48f9d0b84101dc7"
SRC_URI[sha256sum] = "e5bd5a4fa543e9438ad59399d275681c7db0840fed2c531383ad81d5d8b5552f"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

S = "${WORKDIR}/${PN}"

RDEPENDS_${PN} = "libudev cpio util-linux kmod"

do_install () {
        install -d ${D}/etc
        cp -a bash_completion.d ${D}/etc
        cp -a kernel ${D}/etc
        install -d ${D}/etc/initramfs-tools
        install -D conf/* ${D}/etc/initramfs-tools/

        install -d ${D}/usr/bin
        install -D lsinitramfs ${D}/usr/bin
        install -d ${D}/usr/sbin
        install -D mkinitramfs update-initramfs ${D}/usr/sbin
        install -d ${D}/usr/share/${PN}
        cp -a hooks scripts init ${D}/usr/share/${PN}
        install -D hook-functions ${D}/usr/share/${PN}

        install -d ${D}/usr/share/doc/${PN}
        install -D docs/* ${D}/usr/share/doc/${PN}/
        install -D HACKING ${D}/usr/share/doc/${PN}/

        install -d ${D}/usr/share/man/man5
        install -d ${D}/usr/share/man/man8
        install -D *.5 ${D}/usr/share/man/man5
        install -D *.8 ${D}/usr/share/man/man8
}
