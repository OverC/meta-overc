do_install_append() {
    sed -i -e 's/\(After=.*$\)/\1 systemd-resolved.service/' ${D}${systemd_unitdir}/system/dnsmasq.service
}
