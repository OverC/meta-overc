DESCRIPTION = "A simple, fast, and fun package for building command line apps in Go"
HOMEPAGE = "https://github.com/urfave"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=c542707ca9fc0b7802407ba62310bd8f"

SRCNAME = "cli"

PKG_NAME = "github.com/urfave/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;destsuffix=git/src/${PKG_NAME}"

SRCREV = "4ed366e2011dfb9efa3399c709af0976d5e87db4"
PV = "v1.17.0+git${SRCPV}"

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

SYSROOT_PREPROCESS_FUNCS += "go_urfave_cli_file_sysroot_preprocess"

go_urfave_cli_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"