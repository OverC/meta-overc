FILESEXTRAPATHS_prepend := "${THISDIR}:${THISDIR}/linux-yocto:"

SRC_URI += "file://builder.cfg"
SRC_URI += "file://xt-checksum.scc \
            file://ebtables.scc \
            file://vswitch.scc \
            file://lxc.scc \
            file://docker.scc \
            file://criu.scc \
            "
KERNEL_FEATURES_append = " features/kvm/qemu-kvm-enable.scc"
KERNEL_FEATURES_append = " features/nfsd/nfsd-enable.scc"
KERNEL_FEATURES_append = " cfg/systemd.scc"
KERNEL_FEATURES_append = " cfg/fs/ext3.scc"
KERNEL_FEATURES_append = " cfg/fs/ext2.scc"
