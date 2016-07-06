FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "\
	file://Add-noexit-option.patch \
	file://Add-nocheckpid-option.patch \
	"