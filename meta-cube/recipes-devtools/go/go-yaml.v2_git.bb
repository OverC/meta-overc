DESCRIPTION = "YAML support for the Go language"
HOMEPAGE = "https://github.com/go-yaml/yaml"
SECTION = "devel/go"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=6964839e54f4fefcdae13f22b92d0fbb"

SRCNAME = "yaml"

PKG_NAME = "github.com/go-yaml/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=v2;destsuffix=git/src/${PKG_NAME}"

SRCREV = "a3f3340b5840cee44f372bddb5880fcbc419b46a"
PV = "v2.0+git${SRCPV}"

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

SYSROOT_PREPROCESS_FUNCS += "go_yaml_v2_file_sysroot_preprocess"

go_yaml_v2_file_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"

CLEANBROKEN = "1"