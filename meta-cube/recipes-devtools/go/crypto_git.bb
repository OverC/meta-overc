DESCRIPTION = "A golang library of common cryptographic constants"
HOMEPAGE = "https://github.com/golang/crypto/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

PKG_NAME = "github.com/golang/crypto"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "1e856cbfdf9bc25eefca75f83f25d55e35ae72e0"

S = "${WORKDIR}/git"

#Note: As we are using a git mirror of the repo we setup the directory
#with the original Mercurial repo name
do_install() {
        install -d ${D}${prefix}/local/go/src/golang.org/x/crypto
        cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/golang.org/x/crypto/
}

SYSROOT_PREPROCESS_FUNCS += "go_net_sysroot_preprocess"

go_net_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/golang.org/x/crypto
    cp -a ${D}${prefix}/local/go/src/golang.org/x/crypto ${SYSROOT_DESTDIR}${prefix}/local/go/src/golang.org/x/
}

FILES_${PN} += "${prefix}/local/go/src/golang.org/x/crypto/*"
