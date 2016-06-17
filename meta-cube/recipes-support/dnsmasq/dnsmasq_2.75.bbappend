do_install_append() {
    sed -i -e 's/\(After=.*$\)/\1 systemd-resolved.service/' ${D}${systemd_unitdir}/system/dnsmasq.service
    sed -i -e '/^ExecStart.*$/a\Restart=on-failure\nRestartSec=30s' ${D}${systemd_unitdir}/system/dnsmasq.service
}
