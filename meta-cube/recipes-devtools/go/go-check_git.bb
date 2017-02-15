DESCRIPTION = "Rich testing for the Go language"
HOMEPAGE = "https://github.com/go-check/check"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=598d6673548efc92a7d6dfb739add1ed"

SRCNAME = "check"

PKG_NAME = "github.com/go-check/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=v1;destsuffix=git/src/${PKG_NAME}"

SRCREV = "20d25e2804050c1cd24a7eea1e7a6447dd0e74ec"
PV = "v0.1+git${SRCPV}"

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

SYSROOT_PREPROCESS_FUNCS += "go_check_file_sysroot_preprocess"

go_check_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"