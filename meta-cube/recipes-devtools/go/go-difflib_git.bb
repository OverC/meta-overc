DESCRIPTION = "Partial port of Python difflib package to Go"
HOMEPAGE = "https://github.com/pmezard/go-difflib"
SECTION = "devel/go"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=e9a2ebb8de779a07500ddecca806145e"

SRCNAME = "go-difflib"

PKG_NAME = "github.com/pmezard/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=master;destsuffix=git/src/${PKG_NAME}"

SRCREV = "4d60064e8c5ac716dd73fad04e5e72cb5ce0d2c5"
PV = "v1.0.0+git${SRCPV}"

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

SYSROOT_PREPROCESS_FUNCS += "go_difflib_file_sysroot_preprocess"

go_difflib_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"