DESCRIPTION = "Work with containers' images"
HOMEPAGE = "https://github.com/containers/image"
SECTION = "devel/go"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=7e611105d3e369954840a6668c438584"

SRCNAME = "image"

PKG_NAME = "github.com/containers/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;destsuffix=git/src/${PKG_NAME}"

SRCREV = "ecdd233c842aefc26e342c144eef66e6b93b5f47"
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

SYSROOT_PREPROCESS_FUNCS += "containers_image_file_sysroot_preprocess"

containers_image_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"