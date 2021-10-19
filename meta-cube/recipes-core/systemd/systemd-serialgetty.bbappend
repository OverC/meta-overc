python () {
    consoles = d.getVar('SERIAL_CONSOLES', True).split()
    conffiles = ""
    for i in consoles:
        console = i.split(';')[1]
        conffiles += " %s/systemd/system/getty.target.wants/serial-getty@%s.service" % (d.getVar('sysconfdir', True), console)
    
    sysdir = d.getVar('systemd_unitdir', True)
    conffiles += " %s/system/serial-getty@.service" % sysdir
    d.setVar('CONFFILES', conffiles)
}

do_install:append() {
	# for the case with no serial console include
	# stub for serial-getty for runtime configuration
	if [ -z "${SERIAL_CONSOLES}" ] ; then
		default_baudrate=115200
		install -d ${D}${systemd_unitdir}/system/
		install -d ${D}${sysconfdir}/systemd/system/getty.target.wants/
		install -m 0644 ${WORKDIR}/serial-getty@.service ${D}${systemd_unitdir}/system/
		sed -i -e s/\@BAUDRATE\@/$default_baudrate/g ${D}${systemd_unitdir}/system/serial-getty@.service
	fi

}
