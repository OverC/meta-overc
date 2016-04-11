FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://Exit-when-the-file-associated-with-an-FD-is-destroye.patch \
    "
