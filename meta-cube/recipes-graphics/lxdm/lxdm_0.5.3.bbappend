RDEPENDS:${PN} += "packagegroup-xfce-base \
                   packagegroup-core-x11-base \
		  "

do_install:append() {
    sed -i -e 's,skip_password=.*,skip_password=0,' ${D}${sysconfdir}/lxdm/lxdm.conf
    sed -i -e 's,^disable=0,disable=1,' ${D}${sysconfdir}/lxdm/lxdm.conf
    sed -i -e 's,^# session=/usr/bin/startlxde,session=/usr/bin/startxfce4,' ${D}${sysconfdir}/lxdm/lxdm.conf
}
