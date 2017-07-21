# Temporary fix for meta-oe
# Work around error:
#    file /usr/share/polkit-1/rules.d conflicts between attempted installs of gvfs-1.32.1-r0.1.core2_64 and polkit-0.113-r0.8.core2_64
#    file /usr/share/polkit-1/rules.d conflicts between attempted installs of libvirt-1.3.5-r0.9.core2_64 and gvfs-1.32.1-r0.1.core2_64

DEPENDS += "polkit shadow-native"

# Fix up permissions on polkit rules.d to work with rpm4 constraints
do_install_append() {
       chmod 700 ${D}/${datadir}/polkit-1/rules.d
       chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
}

