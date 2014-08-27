#
# Ensure we are compatible with corei7 (and anything else).
# Default is: 
#   COMPATIBLE_MACHINE = "(qemuarm|qemux86|qemuppc|qemumips|qemumips64|qemux86-64)"

COMPATIBLE_MACHINE = "${MACHINE}"

#
# Grab our config fragment.
#
FILESEXTRAPATHS_prepend := "${THISDIR}:"
SRC_URI += "file://builder.cfg"
