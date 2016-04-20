DESCRIPTION = "syscall binding for Go"
HOMEPAGE = "https://go.googlesource.com/sys"
SECTION = "devel/go"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRCNAME = "sys"

PKG_NAME = "golang.org/x/${SRCNAME}"
SRC_URI = "git://github.com/golang/sys"

SRC_URI[md5sum] = "8b50aa373f3cf19efcbc28d76127e59e"
SRC_URI[sha256sum] = "656855b1408abe66cc6a7e58fda06c8f187ec78cb435f6dd043c157e81b30217"

SRCREV = "f64b50fbea64174967a8882830d621a18ee1548e"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_x_sys_sysroot_preprocess"

go_x_sys_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
