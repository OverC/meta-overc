DESCRIPTION = "Pristinely disassembling a tar archive, and stashing needed raw bytes and offsets to reassemble a validating original archive"
HOMEPAGE = "https://github.com/vbatts/tar-split"
SECTION = "devel/go"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=19cd0ecd0a6c26052aec628b72e72881"

SRCNAME = "tar-split"

PKG_NAME = "github.com/vbatts/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=master;destsuffix=git/src/${PKG_NAME}"

SRCREV = "bd4c5d64c3e9297f410025a3b1bd0c58f659e721"
PV = "v0.10.1+git${SRCPV}"

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

SYSROOT_PREPROCESS_FUNCS += "go_tar_split_file_sysroot_preprocess"

go_tar_split_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"