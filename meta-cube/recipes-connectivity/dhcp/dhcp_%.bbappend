# In meta-overc, the system created a bridge named br0 and added 
# the interface eth0 into br0 which will get an ip address when
# system boot up, thus we should disable the dhclient to assign
# ip address for eth0, otherwise both of the br0 and eth0 will
# get ip address and cause the network cannot reach out.

SYSTEMD_AUTO_ENABLE_${PN}-client_forcevariable = "disable"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://dhclient-Add-option-to-die-when-the-parent-process-e.patch"
