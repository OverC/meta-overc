HOMEPAGE = "https://github.com/openSUSE/umoci"
SUMMARY = "umoci modifies Open Container images"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

RDEPENDS_${PN} = "skopeo \
                 "

SRCREV_umoci = "6e68a6a2ae3e12ad78110ab9e68aa56f8281498f"
SRC_URI = "git://github.com/openSUSE/umoci;branch=master;name=umoci;destsuffix=git/src/github.com/openSUSE/umoci \
          "

PV = "v0.3.1-dev+git${SRCPV}"
S = "${WORKDIR}/git/src/github.com/openSUSE/umoci"
GO_IMPORT = "github.com/openSUSE/umoci"

inherit goarch
inherit go

# This disables seccomp and apparmor, which are on by default in the
# go package. 
EXTRA_OEMAKE="BUILDTAGS=''"

do_compile() {
	export GOARCH="${TARGET_GOARCH}"
	export GOPATH="${WORKDIR}/git/"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	cd ${S}

	oe_runmake umoci
}

do_install() {
	install -d ${D}/${sbindir}
	install ${S}/umoci ${D}/${sbindir}
}

INSANE_SKIP_${PN} += "ldflags already-stripped"
