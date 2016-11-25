FILESEXTRAPATHS_prepend := "${THISDIR}:${THISDIR}/linux-yocto:"

SRC_URI += '${@bb.utils.contains("OVERC_PLATFORM_TUNING", "builder", "file://builder.cfg ", "",d)}'
SRC_URI += "file://xt-checksum.scc \
            file://ebtables.scc \
            file://vswitch.scc \
            file://lxc.scc \
            file://docker.scc \
            file://criu.scc \
	    file://uncontain.scc \
            "
KERNEL_FEATURES_append = " features/kvm/qemu-kvm-enable.scc"
KERNEL_FEATURES_append = " cfg/systemd.scc"
KERNEL_FEATURES_append = " cfg/fs/ext3.scc"
KERNEL_FEATURES_append = " cfg/fs/ext2.scc"

# we are ok with version mismatches, since AUTOREV is frequently used
deltask kernel_version_sanity_check
