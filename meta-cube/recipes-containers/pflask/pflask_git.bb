SECTION = "devel"
SUMMARY = "Light weight container runtime"
DESCRIPTION =  "Light-weight container runtime for system or app containers"
HOMEPAGE = "https://ghedo.github.io/pflask/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=c2cd5f772e6f9b401d92014b0d1ebccd"

SRC_URI = "git://github.com/ghedo/pflask.git; \
           file://Makefile \
           file://pflask.mk \
           file://0001-pflask-add-pivot_root-support.patch \
           file://0001-pty-add-retry-for-read-errors.patch \
           file://0001-pflask-add-CLONE_UNCONTAIN-to-clone-flags.patch \
           file://0002-Add-escape-char-command-line-argument.patch \
           file://0003-Allow-changing-of-the-machine-name-on-create.patch \
           file://0004-Add-ability-to-launch-a-CONSOLE_CMD-after-the-server.patch \
           file://0005-Allow-detached-mode-to-start-from-systemd.patch \
           file://0006-Fix-segementation-fault-if-TERM-variable-is-not-set.patch \
           file://0001-netif-Add-support-for-a-wild-card-interface-with-the.patch \
           file://0001-pflask-support-move-wireless-netwrok-to-another-netn.patch \
           file://0001-pflask-add-hook-support.patch \
	   file://0001-pflask-attach-Terminate-attach-when-the-parent-pflas.patch \
	   file://0001-machined-interface-Use-RegisterMachine-instead-of-Cr.patch \
	"

SRCREV="38a7de2d6353d62ce325a5b1f0075adf76fe982c"

RDEPENDS_${PN} = "bash"

inherit pkgconfig

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "dbus"
PACKAGECONFIG[dbus] = ",, dbus, dbus"

EXTRA_DEFINES_${PN} = "${@bb.utils.contains('PACKAGECONFIG','dbus','HAVE_DBUS', '', d)}"

EXTRA_OEMAKE = "\
    'CC=${CC}' \
    'CFLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
    'LINKFLAGS=${LDFLAGS}' \
    'PREFIX=${D}' \
"

# we copy our build parts into the upstream project
do_configure_prepend () {
        cp ${WORKDIR}/Makefile ${S}
        cp ${WORKDIR}/pflask.mk ${S}
}

do_compile() {
 :
}

do_install() {
	set +e

	# The Makefile for pflask isn't all that smart, and doesn't do dependencies
	# quite right. So when you install, things will actually rebuild. So we skip
	# the compile phase, and head right here.
	echo ${PACKAGECONFIG} | grep -q dbus
	if [ $? -eq 0 ]; then
		extra_cflags=$(pkg-config --cflags dbus-1)
		extra_ldflags=$(pkg-config --libs dbus-1)
	fi

	oe_runmake install DEFINES="${EXTRA_DEFINES_${PN}}" CFLAGS="${CFLAGS} ${extra_cflags}" LIB_SH="${extra_ldflags}"
}

