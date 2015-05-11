#
# Ensure we are compatible with corei7 (and anything else).
# Default is: 
#   COMPATIBLE_MACHINE = "(qemuarm|qemux86|qemuppc|qemumips|qemumips64|qemux86-64)"

COMPATIBLE_MACHINE = "${MACHINE}"

#
# Grab our config fragment.
#
FILESEXTRAPATHS_prepend := "${THISDIR}:${THISDIR}/linux-yocto:"
SRC_URI += "file://builder.cfg"

SRC_URI += "file://xt-checksum.scc \
            file://ebtables.scc \
            file://vswitch.scc \
            file://lxc.scc \
            file://docker.scc \
            file://criu.scc \
            "

# was LINUX_VERSION_EXTENSION ?= "-yoctodev-${LINUX_KERNEL_TYPE}"
LINUX_VERSION_EXTENSION = "-pod"

KERNEL_FEATURES_append = " features/kvm/qemu-kvm-enable.scc"

KERNEL_MODULE_AUTOLOAD += "openvswitch"
KERNEL_MODULE_AUTOLOAD += "kvm"
KERNEL_MODULE_AUTOLOAD += "kvm-amd"
KERNEL_MODULE_AUTOLOAD += "kvm-intel"

KERNEL_FEATURES_append += "${@base_contains('DISTRO_FEATURES', 'aufs', ' features/aufs/aufs-enable.scc', '', d)}"
KERNEL_FEATURES_append = " cfg/systemd.scc"
