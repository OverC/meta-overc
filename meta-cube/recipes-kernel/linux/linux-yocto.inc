SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"
FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto-${SHRT_VER}:${THISDIR}:${THISDIR}/linux-yocto:"

SRC_URI += '${@bb.utils.contains("OVERC_PLATFORM_TUNING", "builder", "file://builder.cfg ", "",d)}'
SRC_URI += "file://xt-checksum.scc \
            file://ebtables.scc \
            file://vswitch.scc \
            file://lxc.scc \
            file://docker.scc \
            file://criu.scc \
            file://virt.scc \
            file://uncontain.scc \
            "
KERNEL_FEATURES_append = " features/kvm/qemu-kvm-enable.scc"
KERNEL_FEATURES_append = " features/nfsd/nfsd-enable.scc"
KERNEL_FEATURES_append = " features/tmpfs/tmpfs-posix-acl.scc"
KERNEL_FEATURES_append = " features/cgroups/cgroups.scc"
KERNEL_FEATURES_append = " cfg/systemd.scc"
KERNEL_FEATURES_append = " cfg/fs/ext3.scc"
KERNEL_FEATURES_append = " cfg/fs/ext2.scc"

# we trust the latest!
SRCREV_machine_${MACHINE}="${AUTOREV}"
SRCREV_meta="${AUTOREV}"

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
