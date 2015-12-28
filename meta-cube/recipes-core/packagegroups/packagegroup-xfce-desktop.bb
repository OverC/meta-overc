SUMMARY = "All packages for full desktop installation"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PR = "r0"

inherit packagegroup

RPROVIDES_${PN} += "xfce-desktop"

RDEPENDS_${PN} = " \
    packagegroup-xfce \
"

#Setting the default.target to graphical.target is done in image.bbclass 
#once the IMAGE_FEATURES contains "x11-base". Since for headless images,  
#IMAGE_FEATURES didn't contain it, so reset the default.target to graphical.target
#after this packagegroup is installed for headless system.

pkg_postinst_${PN}() {
#!/bin/sh -e
ln -sf /lib/systemd/system/graphical.target /etc/systemd/system/default.target
}
