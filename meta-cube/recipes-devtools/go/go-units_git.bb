DESCRIPTION = "Parse and print size and time units in human-readable format"
HOMEPAGE = "https://github.com/docker/go-units"
SECTION = "devel/go"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=04424bc6f5a5be60691b9824d65c2ad8"

SRCNAME = "go-units"

PKG_NAME = "github.com/docker/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=master;destsuffix=git/src/${PKG_NAME}"

SRCREV = "9e638d38cf6977a37a8ea0078f3ee75a7cdb2dd1"
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

SYSROOT_PREPROCESS_FUNCS += "go_units_file_sysroot_preprocess"

go_units_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"