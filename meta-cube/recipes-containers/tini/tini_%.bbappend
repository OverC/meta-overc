do_install_append() {
    install -d ${D}/sbin
    ln -sfr ${D}${bindir}/docker-init ${D}/sbin/init
}
