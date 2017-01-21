SUMMARY = "Go package providing a key/value store"
HOMEPAGE = "https://github.com/boltdb/bolt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13b2a308eefa10d841e3bf2467dbe07a"

PKG_NAME = "github.com/boltdb/bolt"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "be4e606bc48012421bc90eda58a8aad7a95aaf83"

SYSROOT_PREPROCESS_FUNCS += "bolt_sysroot_preprocess"

inherit golang

do_compile_prepend() {
    export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"
}

do_compile_append() {
    go install github.com/boltdb/bolt/cmd/bolt
}

bolt_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
    install -d ${SYSROOT_DESTDIR}${prefix}/bin
    cp -a ${D}${prefix}/bin/* ${SYSROOT_DESTDIR}${prefix}/bin/
}

CLEANBROKEN = "1"

