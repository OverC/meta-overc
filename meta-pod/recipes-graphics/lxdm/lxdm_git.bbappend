do_install_append() {
    sed -i -e 's,skip_password=.*,skip_password=0,' ${D}${sysconfdir}/lxdm/lxdm.conf
    sed -i -e 's,^disable=0,disable=1,' ${D}${sysconfdir}/lxdm/lxdm.conf
}
