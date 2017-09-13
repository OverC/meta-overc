HOMEPAGE = "https://github.com/projectatomic/skopeo"
SUMMARY = "Work with remote images registries - retrieving information, images, signing content"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=7e611105d3e369954840a6668c438584"

DEPENDS = "\
           go-urfave \
           containers-image \
           go-digest \
           go-cheggaaa-pb.v1 \
           containers-storage \
           go-logrus \
           go-check \
           go-testify \
           go-spew \
           go-difflib \	
           go-errors \
           crypto \
           go-connections \
           go-tar-split \
           go-context \
           go-mux \
           go-units \
           go-net \
           go-distribution \
           go-libtrust \
           oci-image-spec \
           go-gpgme \
           go-glog \
           go-client-go \
           go-yaml \
           go-yaml.v2 \
           go-mergo \
           go-zfs \
           go-uuid \
           gpgme \
           multipath-tools \
           btrfs-tools \
          "

inherit go

RDEPENDS_${PN} = "gpgme \
                  lvm2 \
                  libgpg-error \
                  libassuan \
                 "

SRC_URI = "git://github.com/projectatomic/skopeo"
SRCREV = "f89bd82dcd05a8e09b770bac00d5731c33db5cb1"
PV = "v0.1.19-dev+git${SRCPV}"
GO_IMPORT = "import"

S = "${WORKDIR}/git"

inherit goarch

# This disables seccomp and apparmor, which are on by default in the
# go package. 
EXTRA_OEMAKE="BUILDTAGS=''"

do_compile() {
	export GOARCH="${TARGET_GOARCH}"

	# Setup vendor directory so that it can be used in GOPATH.
	#
	# Go looks in a src directory under any directory in GOPATH but riddler
	# uses 'vendor' instead of 'vendor/src'. We can fix this with a symlink.
	#
	# We also need to link in the ipallocator directory as that is not under
	# a src directory.
	ln -sfn . "${S}/src/import/vendor/src"
	mkdir -p "${S}/src/import/vendor/src/github.com/projectatomic/skopeo"
	ln -sfn "${S}/src/import/skopeo" "${S}/src/import/vendor/src/github.com/projectatomic/skopeo"
	ln -sfn "${S}/src/import/version" "${S}/src/import/vendor/src/github.com/projectatomic/skopeo/version"
	export GOPATH="${S}/src/import/vendor"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	cd ${S}/src/import

	oe_runmake binary-local
}

do_install() {
	install -d ${D}/${sbindir}
	install -d ${D}/${sysconfdir}/containers

	install ${S}/src/import/skopeo ${D}/${sbindir}/
	install ${S}/src/import/default-policy.json ${D}/${sysconfdir}/containers/policy.json
}

INSANE_SKIP_${PN} += "ldflags"
