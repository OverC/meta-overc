SUMMARY = "All packages for full desktop installation"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r0"

inherit packagegroup

RPROVIDES:${PN} += "xfce-desktop"

RDEPENDS:${PN} = " \
    packagegroup-xfce \
"

#Setting the default.target to graphical.target is done in image.bbclass 
#once the IMAGE_FEATURES contains "x11-base". Since for headless images,  
#IMAGE_FEATURES didn't contain it, so reset the default.target to graphical.target
#after this packagegroup is installed for headless system.

pkg_postinst:${PN}() {
#!/bin/sh -e
ln -sf ${systemd_unitdir}/system/graphical.target /etc/systemd/system/default.target
}
