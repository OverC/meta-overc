DESCRIPTION = "Go wrapper for the GPGME library"
HOMEPAGE = "https://github.com/mtrmac/gpgme"
SECTION = "devel/go"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=355c5863b4fdc0f2cb11e01c9db1e1cc"

SRCNAME = "gpgme"

PKG_NAME = "github.com/mtrmac/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=master;destsuffix=git/src/${PKG_NAME}"

SRCREV = "f03421e26925ca72d1e07098d25cb632151df79d"
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

SYSROOT_PREPROCESS_FUNCS += "go_gpgme_file_sysroot_preprocess"

go_gpgme_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"