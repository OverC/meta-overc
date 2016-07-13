DEPENDS += "go-cross"

S = "${WORKDIR}/git"

do_compile() {
    #Setting up GOPATH to find deps (including those already in consul)
    cd ${S}
    rm -rf .gopath
    mkdir -p .gopath/src/$(dirname ${PKG_NAME})
    ln -sf ../../../.. .gopath/src/${PKG_NAME}
    export GOPATH=${S}:${STAGING_DIR_TARGET}/${prefix}/local/go:${S}/.gopath
    export GOARCH="${TARGET_ARCH}"
    export CGO_ENABLED="1"

    # avoid using the default '/var/tmp'
    export TMPDIR=${WORKDIR}/build-tmp
    mkdir -p ${WORKDIR}/build-tmp

    # supported amd64, 386, arm
    if [ "${TARGET_ARCH}" = "x86_64" ]; then
        export GOARCH="amd64"
    elif [ "${TARGET_ARCH}" = "i586" ]; then
        export GOARCH="386"   
    fi
    go install ${PKG_NAME}
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cd ${S}
    for file in $(find * -type f); do
        install -m 0644 -D ${file} ${D}${prefix}/local/go/src/${PKG_NAME}/${file}
    done

    install -d ${D}${bindir}
    # golang programs will install their binary files into
    # an arch named sub directory when do corss building,
    # such as when the host is amd64 arch and the target arch
    # is arm, then it will install his binary into bin/linux_arm
    # directory, so here will cp them from this sub directory
    # do the standard binary directory.
    if [ -d ${S}/.gopath/bin/linux_* ]; then
        src=${S}/.gopath/bin/linux_*/
    else
        src=${S}/.gopath/bin/
    fi
    cd ${src}
    for file in $(find * -type f); do
        install -D ${file} ${D}${bindir}/${file}
    done
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
