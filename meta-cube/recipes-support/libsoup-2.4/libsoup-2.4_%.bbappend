FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-add-soup_uri_to_string_with_password.patch \
"

DEPENDS += "glib-networking"

BBCLASSEXTEND = "native"
