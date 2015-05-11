FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://polkit-use-instead-of-hardcoded-lib.patch"
