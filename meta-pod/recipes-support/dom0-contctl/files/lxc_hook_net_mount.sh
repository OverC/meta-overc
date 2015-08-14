#!/bin/bash

source $(dirname ${0})/lxc_driver_net.sh


lxc_set_net_cn_end_options ${LXC_CONFIG_FILE} ${LXC_NAME} "" ${LXC_ROOTFS_MOUNT}
exit 0
