SUMMARY = "All packages for audio support."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-audio \
    "

RDEPENDS:${PN} = "\
    alsa-utils \
    pulseaudio \
    pulseaudio-server \
    pavucontrol \
    xfce4-pulseaudio-plugin \
    "
