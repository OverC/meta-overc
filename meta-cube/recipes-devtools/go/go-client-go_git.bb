DESCRIPTION = "Go client for Kubernetes."
HOMEPAGE = "https://github.com/kubernetes/client-go"
SECTION = "devel/go"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCNAME = "client-go"

PKG_NAME = "github.com/kubernetes/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=master;destsuffix=git/src/${PKG_NAME}"

SRCREV = "17c583b14204b54f8924a6b78339d74a8e902c0f"
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

SYSROOT_PREPROCESS_FUNCS += "go_client_go_file_sysroot_preprocess"

go_client_go_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"