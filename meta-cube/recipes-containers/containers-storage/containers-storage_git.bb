DESCRIPTION = "provide methods for storing filesystem layers, container images, and containers"
HOMEPAGE = "https://github.com/containers/storage"
SECTION = "devel/go"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=aadc30f9c14d876ded7bedc0afd2d3d7"

SRCNAME = "storage"

PKG_NAME = "github.com/containers/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;destsuffix=git/src/${PKG_NAME}"

SRCREV = "850e2bcf3b7db30178e47cc2c4b839c580a0d007"
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

SYSROOT_PREPROCESS_FUNCS += "containers_storage_file_sysroot_preprocess"

containers_storage_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"