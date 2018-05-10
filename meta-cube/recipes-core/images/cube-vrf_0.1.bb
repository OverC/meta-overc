SUMMARY = "System Virtual Routing & Forwarding (VRF) container"
DESCRIPTION = "In an effort to keep cube-essential small while \
ensuring cube-dom0 remains focused on container management system \
routing functions are managed by this container."

HOMEPAGE = "http://www.windriver.com"

require recipes-core/images/c3-app-container.inc

IMAGE_INSTALL += " \
    openvswitch \
    iproute2 \
    dnsmasq \
    base-passwd \
    vrf-init \
    bash \
"

ROOTFS_POSTPROCESS_COMMAND += "create_cube_mount_dirs; "

create_cube_mount_dirs () {
    mkdir -p ${IMAGE_ROOTFS}/opt/container
    mkdir -p ${IMAGE_ROOTFS}/var/lib/cube
}
