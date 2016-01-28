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
