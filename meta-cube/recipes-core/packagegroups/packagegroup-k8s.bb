SUMMARY = "All packages for kubernetes compute node"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-k8s-compute \
    packagegroup-k8s-master \
    "

RDEPENDS_packagegroup-k8s-compute = " \
    packagegroup-container \
    packagegroup-docker \
    docker-registry \
    cri-o \
    cri-o-config \
    kubeadm \
    kubectl \
    kubelet \
    kube-proxy \
"

RDEPENDS_packagegroup-k8s-master = " \
    packagegroup-container \
"
