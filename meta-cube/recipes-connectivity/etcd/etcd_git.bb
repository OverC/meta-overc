DESCRIPTION = "A distributed key-value store for shared config and service discovery"
HOMEPAGE = "https://github.com/coreos/etcd"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PKG_NAME = "github.com/coreos/etcd"
SRC_URI = "git://${PKG_NAME}.git;branch=release-3.3 \
          "

SRCREV = "fca8add78a9d926166eb739b8e4a124434025ba3"
PV = "3.3.9+git${SRCPV}"
GO_IMPORT = "import"

TARGET_CC_ARCH += "${LDFLAGS}"

S = "${WORKDIR}/git"

inherit go
inherit goarch

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
	ln -sfn . "${S}/src/import/cmd/vendor/src"
	mkdir -p "${S}/src/import/cmd/vendor/src/github.com/cockroachdb/cmux"
	ln -sfn "${S}/cmux" "${S}/src/import/cmd/vendor/github.com/cockroachdb/cmux"
	export GOPATH="${S}/src/import/cmd/vendor"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	if [ "${TARGET_ARCH}" = "x86_64" ]; then
		export GOARCH="amd64"
	elif [ "${TARGET_ARCH}" = "i586" ]; then
		export GOARCH="386"
	fi

	./src/import/build
}

# TODO: we could adduser etcd
do_install() {
	install -d ${D}/${bindir}
	install ${S}/src/import/bin/etcd ${D}/${bindir}/etcd
	install ${S}/src/import/bin/etcdctl ${D}/${bindir}/etcdctl

	install -d ${D}${systemd_unitdir}/system/
	install -m 0644 ${S}/src/import/contrib/systemd/etcd.service ${D}${systemd_unitdir}/system/

	# make sure etcd is willing to listen for more than localhost
	sed -i 's%ExecStart.*%ExecStart=/usr/bin/etcd -listen-client-urls=http://0.0.0.0:2379,http://0.0.0.0:4001 -advertise-client-urls http://0.0.0.0:2379,http://0.0.0.0:4001%g' ${D}${systemd_unitdir}/system/etcd.service

	# etcd state is in /var/lib/etcd
	install -d ${D}/${localstatedir}/lib/${BPN}

	# we aren't creating a user, so we need to comment out this line
	sed -i '/User/s/^/#/' ${D}${systemd_unitdir}/system/etcd.service
}

deltask compile_ptest_base
