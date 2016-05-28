SUMMARY = "All packages for audio support."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-audio \
    "

RDEPENDS_${PN} = "\
    alsa-utils \
    pulseaudio \
    pavucontrol \
    xfce4-pulseaudio-plugin \
    "
