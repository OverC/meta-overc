SUMMARY = "Basic init for initramfs to mount and pivot root"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SRC_URI = "file://init-server.sh"

PR = "r9"

RDEPENDS_${PN} = "parted e2fsprogs-mke2fs"

do_configure() {
}

do_install() {
        install -m 0755 ${WORKDIR}/init-server.sh ${D}/init

	# Create device nodes expected by some kernels in initramfs
	# before even executing /init.
	install -d ${D}/dev
	mknod -m 622 ${D}/dev/console c 5 1
}

# While this package maybe an allarch due to it being a 
# simple script, reality is that it is Host specific based
# on the COMPATIBLE_HOST below, which needs to take precedence
#inherit allarch
INHIBIT_DEFAULT_DEPS = "1"

FILES_${PN} = " /init /dev"

COMPATIBLE_HOST = "(arm|aarch64|i.86|x86_64|powerpc).*-linux"
