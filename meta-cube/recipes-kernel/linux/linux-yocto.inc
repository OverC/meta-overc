#SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"
#FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto-${SHRT_VER}:${THISDIR}:${THISDIR}/linux-yocto:"
FILESEXTRAPATHS:prepend := "${THISDIR}:${THISDIR}/linux-yocto:"

SRC_URI += '${@bb.utils.contains("OVERC_PLATFORM_TUNING", "builder", "file://builder.cfg ", "",d)}'
SRC_URI += "file://xt-checksum.scc \
            file://ebtables.scc \
            file://vswitch.scc \
            file://lxc.scc \
            file://docker.scc \
            file://criu.scc \
            file://virt.scc \
            file://k8s.scc \
            "
# this is not only config, but a patch ...
SRC_URI:append = "${@bb.utils.contains('DISTRO', 'overc', 'file://uncontain.scc', '', d )}"

KERNEL_FEATURES:append = " features/kvm/qemu-kvm-enable.scc"
KERNEL_FEATURES:append = " features/nfsd/nfsd-enable.scc"
KERNEL_FEATURES:append = " features/tmpfs/tmpfs-posix-acl.scc"
KERNEL_FEATURES:append = " features/cgroups/cgroups.scc"
KERNEL_FEATURES:append = " cfg/systemd.scc"
KERNEL_FEATURES:append = " cfg/fs/ext3.scc"
KERNEL_FEATURES:append = " cfg/fs/ext2.scc"

KERNEL_MODULE_AUTOLOAD:append += "nf_conntrack_ipv6 openvswitch"

# kubernetes stuff. maybe hide in a distro variable ?
KERNEL_MODULE_AUTOLOAD:append += "ip_vs_rr ip_vs_wrr ip_vs_sh"
KERNEL_MODULE_AUTOLOAD:append += "vxlan vport-vxlan"
KERNEL_MODULE_AUTOLOAD:append += "br_netfilter"
KERNEL_MODULE_AUTOLOAD:append += "xt_set"
KERNEL_MODULE_AUTOLOAD:append += "ip_set_hash_ip ip_set_hash_netport ip_set_hash_ipportip"
KERNEL_MODULE_AUTOLOAD:append += "ip_set ip_set_hash_ipportnet ip_set_hash_ipmark ip_set_bitmap_ipmac"
KERNEL_MODULE_AUTOLOAD:append += "ip_set_bitmap_port ip_set_hash_netportnet ip_set_hash_mac"
KERNEL_MODULE_AUTOLOAD:append += "ip_set_hash_net ip_set_bitmap_ip ip_set_hash_ipmac ip_set_list_set"
KERNEL_MODULE_AUTOLOAD:append += "ip_set_hash_netiface ip_set_hash_ipport ip_set_hash_netnet"
KERNEL_MODULE_PROBECONF:append += "nf_conntrack nf_conntrack_ipv6"
module_conf_nf_conntrack = "options nf_conntrack hashsize=131072"
module_conf_nf_conntrack_ipv6 = "options nf_conntrack_ipv6 hashsize=131072"

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

            rkis = d.getVar('RDEPENDS:kernel-image', True) or ""
            rkistr = ' '
            for rki in rkis.split():
                if rki != 'kernel-image-' + typelower:
                    rkistr += ' ' + rki

            d.setVar('RDEPENDS:kernel-image', rkistr)
}
