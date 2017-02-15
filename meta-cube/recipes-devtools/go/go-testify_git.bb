DESCRIPTION = "A sacred extension to the standard go testing package"
HOMEPAGE = "https://github.com/stretchr/testify"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=39cd1d751bc25944831de86496e3cf68"

SRCNAME = "testify"

PKG_NAME = "github.com/stretchr/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=master;destsuffix=git/src/${PKG_NAME}"

SRCREV = "332ae0e18f810344d8b345b3bdde660ba1ce0ba7"
PV = "v1.0+git${SRCPV}"

S = "${WORKDIR}/git"

# NO-OP the do compile rule because this recipe is source only.
do_compile() {
}

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	for j in $(cd ${S} && find src/${PKG_NAME} -name "*.go" -not -path "*/.tool/*"); do
	    if [ ! -d ${D}${prefix}/local/go/$(dirname $j) ]; then
	        mkdir -p ${D}${prefix}/local/go/$(dirname $j)
	    fi
	    cp $j ${D}${prefix}/local/go/$j
	done
	cp -r ${S}/src/${PKG_NAME}/LICENSE ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_testify_file_sysroot_preprocess"

go_testify_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"