inherit hosts

ROOTFS_POSTPROCESS_COMMAND += "builder_configure_host ; "

builder_configure_host() {
#    bbnote "builder: configuring host"

    echo "${TARGETNAME}" > ${IMAGE_ROOTFS}/etc/hostname

}
