DESCRIPTION = "A distributed key-value store for shared config and service discovery"
HOMEPAGE = "https://github.com/coreos/etcd"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PKG_NAME = "github.com/coreos/etcd"
SRC_URI = "git://${PKG_NAME}.git \
          "

SRCREV = "99639186cd41eebd3f905935df586a9094a2bfa1"

TARGET_CC_ARCH += "${LDFLAGS}"

inherit golang

# During packaging etcd gets the warning "no GNU hash in elf binary"
# This issue occurs due to compiling without ldflags, but a
# solution has yet to be found. For now we ignore this error with
# the line below.
INSANE_SKIP_${PN} = "ldflags"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "etcd.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

systemd_postinst() {
OPTS=""

if [ -n "$D" ]; then
    OPTS="--root=$D"
fi

if type systemctl >/dev/null 2>/dev/null; then
        systemctl $OPTS ${SYSTEMD_AUTO_ENABLE} ${SYSTEMD_SERVICE}
fi
}



RDEPENDS_${PN} = "bash"

do_compile() {
	export GOARCH="${TARGET_GOARCH}"

	# Setup vendor directory so that it can be used in GOPATH.
	#
	# Go looks in a src directory under any directory in GOPATH but
	# uses 'vendor' instead of 'vendor/src'. We can fix this with a symlink.
	#
	# We also need to link in the ipallocator directory as that is not under
	# a src directory.
	ln -sfn . "${S}/cmd/vendor/src"
	mkdir -p "${S}/cmd/vendor/src/github.com/cockroachdb/cmux"
	ln -sfn "${S}/cmux" "${S}/cmd/vendor/github.com/cockroachdb/cmux"
	export GOPATH="${S}/cmd/vendor"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	./build
}

# TODO: we could adduser etcd
do_install() {
	install -d ${D}/${bindir}
	install ${S}/bin/etcd ${D}/${bindir}/etcd
	install ${S}/bin/etcdctl ${D}/${bindir}/etcdctl

	install -d ${D}/lib/systemd/system/
	install -m 0644 ${S}/contrib/systemd/etcd.service ${D}/lib/systemd/system/

	# etcd state is in /var/lib/etcd
	install -d ${D}/${localstatedir}/lib/${BPN}

	# we aren't creating a user, so we need to comment out this line
	sed -i '/User/s/^/#/' ${D}/lib/systemd/system/etcd.service
}
