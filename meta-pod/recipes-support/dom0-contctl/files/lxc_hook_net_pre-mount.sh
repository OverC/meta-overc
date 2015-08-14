#!/bin/bash

# This script runs in the spawning container's
# net and mount namespaces.

source $(dirname ${0})/lxc_driver_net.sh

which ip > /dev/null 2>&1
[ $? -ne 0 ] && echo "Fatal Error, ip util is not available" && exit 1
conn_list=$(get_lxc_config_option "wr.network.connection" ${LXC_CONFIG_FILE})
[ -z "${conn_list}" ] && exit 0

# Going through each connection
for conn in ${conn_list}; do
    type=$(get_lxc_config_option "wr.network.${conn}.type" ${LXC_CONFIG_FILE})
    if [ "${type}" == "${NET_TYPE_VETH}" ]; then
        cn_eth_name=$(lxc_get_veth_cn_end_name ${conn} ${LXC_NAME})

        # Right now the "cn end" is parked in Domain0 net namespace.
        # Need to jump into Domain0 net namespace and move it over to this
        # container namespace.
        #
        # Its a bit tricky here.  nsenter uses ns files in /proc mount
        # point to switch namespace, while ip netns command uses in kernel
        # net namespace (in other words, ip netns does not use /proc/<pid>/ns
        # at all).
        #
        # At this point, pre-mount stage, this script is running in this
        # spawning container net namespace but under Domain0 /proc mount point.
        # This means that the real process 1 is different with the process
        # pointed by /proc/1/ns.  So "ip netns 1" will jump to the right net
        # namespace of spawning container.
        #
        # Double tricky here is that /proc/1 is not really the process 1 of
        # Domain0 (please refer to launch_peer_container or launch_nested_container
        # function in lxc_driver.sh to see what /proc/1 is pointing to).  So
        # we cannot use "nsenter -n -t 1" here. Luckily, we save Domain0 proc 1
        # at lxc config path (e.g. /var/lib/lxc/<container>/.ctl-dom-proc-1)

        ctl_dom_proc_1_bind_mount_path=$(get_lxc_ctl_dom_proc_1_bind_mount_path ${LXC_NAME})
        nsenter -n -t ${ctl_dom_proc_1_bind_mount_path} -- ip link set ${cn_eth_name} netns 1
    fi
done
lxc_set_net_cn_end_options ${LXC_CONFIG_FILE} ${LXC_NAME}

exit 0
