SUMMARY = "Go wrapper for OpenLDAP Lightning Memory-Mapped Database"
HOMEPAGE = "https://github.com/armon/gomdb"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=371268fbd8183f4cb40621178eb05f28"

PKG_NAME = "github.com/armon/gomdb"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "151f2e08ef45cb0e57d694b2562f351955dff572"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "gombd_sysroot_preprocess"

gombd_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
