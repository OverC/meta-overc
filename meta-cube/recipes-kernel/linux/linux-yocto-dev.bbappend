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
            file://uncontain.scc \
            "

# was LINUX_VERSION_EXTENSION ?= "-yoctodev-${LINUX_KERNEL_TYPE}"
LINUX_VERSION_EXTENSION = "-cube"

KERNEL_FEATURES_append = " features/kvm/qemu-kvm-enable.scc"
KERNEL_FEATURES_append = " features/tmpfs/tmpfs-posix-acl.scc"
KERNEL_FEATURES_append = " features/cgroups/cgroups.scc"

KERNEL_MODULE_AUTOLOAD_append += "nf_conntrack_ipv6 openvswitch"

KERNEL_FEATURES_append += "${@bb.utils.contains('DISTRO_FEATURES', 'aufs', ' features/aufs/aufs-enable.scc', '', d)}"
KERNEL_FEATURES_append = " cfg/systemd.scc"
KERNEL_FEATURES_append = " cfg/fs/ext3.scc"
KERNEL_FEATURES_append = " cfg/fs/ext2.scc"

# we are ok with version mismatches, since AUTOREV is frequently used
deltask kernel_version_sanity_check

# Don't install the normal kernel image if the bundled kernel configured
python __anonymous () {
    if d.getVar('INITRAMFS_IMAGE', True) and \
       d.getVar('INITRAMFS_IMAGE_BUNDLE', True) == '1':

        tfmake = d.getVar('KERNEL_IMAGETYPE_FOR_MAKE', True) or ""

        for type in tfmake.split():
            typelower = type.lower()

            rkis = d.getVar('RDEPENDS_kernel-image', True) or ""
            rkistr = ' '
            for rki in rkis.split():
                if rki != 'kernel-image-' + typelower:
                    rkistr += ' ' + rki

            d.setVar('RDEPENDS_kernel-image', rkistr)
}
