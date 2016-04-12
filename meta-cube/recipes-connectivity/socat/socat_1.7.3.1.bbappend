FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://Exit-when-the-file-associated-with-an-FD-is-destroye.patch \
    "
SRC_URI_remove = "file://0001-Access-c_ispeed-and-c_ospeed-via-APIs.patch"
