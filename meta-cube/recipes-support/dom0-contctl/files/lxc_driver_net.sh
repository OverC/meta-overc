#!/bin/bash

source $(dirname ${0})/lxc_common_helpers.sh

NET_TYPE_VETH="veth"
NET_TYPE_DPDK="dpdk"

DPDK_HUGEFS_HOST_PATH_DEFAULT=$(get_lxc_config_option "lxc.dpdk.hugefs.host_path_default" ${main_config_file})
DPDK_HUGEFS_PATH_DEFAULT=$(get_lxc_config_option "lxc.dpdk.hugefs.path_default" ${main_config_file})
DPDK_HUGEFS_NR_HUGEPAGES=$(get_lxc_config_option "lxc.dpdk.hugefs.nr_hugepages" ${main_config_file})
DPDK_DEFAULT_PCI_DRIVER="igb_uio"

function lxc_get_veth_cn_end_name {
    local conn_name=${1}
    local cn_name=${2}

    echo "veth-${cn_name}-${conn}-0"
}

function lxc_get_veth_remote_end_name {
    local conn_name=${1}
    local cn_name=${2}

    echo "veth-${cn_name}-${conn}-1"
}

function lxc_set_net_cn_end_options {
    local cfg_file=${1}
    local cn_name=${2}
    local cn_init_pid=${3}
    local root_fs_mount=${4}

    conn_list=$(get_lxc_config_option_list "wr.network.connection" ${cfg_file})
    [ -z "${conn_list}" ] && return 1

    # Going through each connection
    for conn in ${conn_list}; do
        type=$(get_lxc_config_option "wr.network.${conn}.type" ${cfg_file})
        if [ "${type}" == "${NET_TYPE_VETH}" ]; then
            nsenter_ext=""
            [ -n "${cn_init_pid}" ] && nsenter_ext="nsenter -n -t ${cn_init_pid} --"

            # At this point connection has been setup for "remote end" veth
            cn_eth_name=$(lxc_get_veth_cn_end_name ${conn} ${cn_name})

            hwaddr=$(get_lxc_config_option "wr.network.${conn}.hwaddr" ${cfg_file})
            if [ -n "${hwaddr}" ]; then
                ${nsenter_ext} ip link set ${cn_eth_name} address ${hwaddr}
                [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set hwaddrs ${hwaddr} to ${cn_eth_name}"
            fi
            ipv4=$(get_lxc_config_option "wr.network.${conn}.ipv4" ${cfg_file})
            if [ -n "${ipv4}" ]; then
                ${nsenter_ext} ip addr add ${ipv4} dev ${cn_eth_name}
                [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set ipv4 ${ipv4} to ${cn_eth_name}"
            fi
            new_name=$(get_lxc_config_option "wr.network.${conn}.name" ${cfg_file})
            if [ -n "${new_name}" ]; then
                ${nsenter_ext} ip link set ${cn_eth_name} name ${new_name}
                if [ $? -eq 0 ]; then
                    cn_eth_name=${new_name}
                else
                    lxc_log "Warning, ${conn}, cannot change name from ${cn_eth_name} to ${new_name}"
                fi
            fi
            flags=$(get_lxc_config_option "wr.network.${conn}.flags" ${cfg_file} | grep 'up')
            if [ -n "${flags}" ]; then
                ${nsenter_ext} ip link set ${cn_eth_name} up
                [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot activate ${cn_eth_name}"
            fi
        elif [ "${type}" == "${NET_TYPE_DPDK}" ]; then
            nsenter_ext=""
            [ -n "${cn_init_pid}" ] && nsenter_ext="nsenter -n -m -p -t ${cn_init_pid} --"

            dpdk_pci_list=$(get_lxc_config_option "wr.network.${conn}.dpdk.pci" ${cfg_file} | sed 's/;/ /g')
            for pci_info in ${dpdk_pci_list}; do
                # pci_info has the following format:  <pci address>,<dpdk uio driver>
                pci=$(${nsenter_ext} echo ${pci_info} | awk -F ',' '{print $1}')

                # Create uio dev
                sys_pci_uio_path=$(${nsenter_ext} find ${root_fs_mount}/sys/ -name "*uio*" | grep -F "${pci}" | grep "uio\/uio")
                pci_dev_major_minor=$(${nsenter_ext} cat ${sys_pci_uio_path}/dev | sed 's/:/ /g')
                ${nsenter_ext} rm ${root_fs_mount}/dev/$(basename ${sys_pci_uio_path}) > /dev/null 2>&1
                ${nsenter_ext} /bin/mknod ${root_fs_mount}/dev/$(basename ${sys_pci_uio_path}) c ${pci_dev_major_minor}
            done

            # Check any of additional dpdk kernel modules might need to have a
            # dev node created.
            dpdk_kernmod_list=$(get_lxc_config_option "wr.network.${conn}.dpdk.kernmod" ${cfg_file} | sed 's/;/ /g')
            # Right now only dpdk rte_kni requires /dev/kni to be created
            res=$(echo ${dpdk_kernmod_list} | grep -F 'rte_kni')
            if [ -n "${res}" ]; then
                sys_kni_path=$(${nsenter_ext} find ${root_fs_mount}/sys/ -name "kni" | grep -F "devices")
                kni_dev_major_minor=$(${nsenter_ext} cat ${sys_kni_path}/dev | sed 's/:/ /g')
                ${nsenter_ext} /bin/mknod ${root_fs_mount}/dev/kni c ${kni_dev_major_minor}
            fi

            # Now we mount hugepage fs into container.  We support 2 modes:
            # "host" makes the host hugepage fs available into container; "private"
            # means the container will have its own hugepage mount.

            dpdk_hugefs_mount=$(get_lxc_config_option "wr.network.${conn}.dpdk.hugefs.mount" ${cfg_file})
            # Default is using host hugepage
            [ -z "${dpdk_hugefs_mount}" ] && dpdk_hugefs_mount="${HOST_CN_NAME}"
            dpdk_hugefs_path=$(get_lxc_config_option "wr.network.${conn}.dpdk.hugefs.path" ${cfg_file})
            [ -z "${dpdk_hugefs_path}" ] && dpdk_hugefs_path=${DPDK_HUGEFS_PATH_DEFAULT}
            if [ "${dpdk_hugefs_mount}" == "${HOST_CN_NAME}" ]; then
                # Can only share with host hugepage fs when this function is invoked from
                # container's hook functions.
                if [ -z "${cn_init_pid}" ]; then
                    /bin/mount -o bind ${DPDK_HUGEFS_HOST_PATH_DEFAULT} ${root_fs_mount}/${dpdk_hugefs_path}
                    res=$?
                    [ $? -ne 0 ] && { lxc_log "Error, cannot bind mount, ${res}"; return ${res}; }
                else
                    lxc_log "Warning, do not support host shared hugepage filesystem"
                fi
            elif [ "${dpdk_hugefs_mount}" == "private" ]; then
                # Currently kernel does not support hugetlbfs min_size option yet.  So for now just
                # use "size" option
                dpdk_hugefs_max_size=$(get_lxc_config_option "wr.network.${conn}.dpdk.hugefs.maxsize" ${cfg_file})
                [ -n "${dpdk_hugefs_max_size}" ] && dpdk_hugefs_options="${dpdk_hugefs_options} -o size=${dpdk_hugefs_max_size}"

                ${nsenter_ext} /bin/mount -t hugetlbfs ${dpdk_hugefs_options} none ${root_fs_mount}/${dpdk_hugefs_path}
                res=$?
                [ ${res} -ne 0 ] && { lxc_log "Error, cannot mount hugetlbfs, ${res}"; return ${res}; }
            fi
        fi
    done
    return 0
}

function lxc_setup_net_cn_end {
    local cfg_file=${1}
    local cn_name=${2}

    # Some basic checks
    [ ! -e "${cfg_file}" ] && return 1
    cn_init_pid=$(get_lxc_init_pid_from_cn_name ${cn_name})
    [ -z "${cn_init_pid}" ] && return 1
    which ip > /dev/null 2>&1
    [ $? -ne 0 ] && lxc_log "Error, ip util is not available" && return 1

    conn_list=$(get_lxc_config_option_list "wr.network.connection" ${cfg_file})
    [ -z "${conn_list}" ] && return 1

    # Going through each connection, at this point,
    # connection has been setup for "remote end" veth
    for conn in ${conn_list}; do
        type=$(get_lxc_config_option "wr.network.${conn}.type" ${cfg_file})
        if [ "${type}" == "${NET_TYPE_VETH}" ]; then
            cn_eth_name=$(lxc_get_veth_cn_end_name ${conn} ${cn_name})
            # Now switch "cn end" to correct namespace
            ip link set ${cn_eth_name} netns ${cn_init_pid}
            [ $? -ne 0 ] && lxc_log "Error, ${conn}, cannot switch ${cn_eth_name} to ${cn_init_pid} net namespace" \
                    && return 1
        fi
    done
    lxc_set_net_cn_end_options ${cfg_file} ${cn_name} ${cn_init_pid}
    if [ $? -ne 0 ]; then
        lxc_remove_net ${cfg_file} ${cn_name}
        return 1
    fi

    return 0
}

# Read config file, and for each connection setup the "remote end" only.
# On first failed to create connection, the function will invoke
# lxc_remove_net to clean up the mess.
function lxc_setup_net_remote_end {
    local cfg_file=${1}
    local cn_name=${2}
    local ret=0

    # Some basic checks
    [ ! -e "${cfg_file}" ] && return 1
    which ip > /dev/null 2>&1
    [ $? -ne 0 ] && lxc_log "Error, ip util is not available" && return 1

    # Get list of connections specified in cfg file.
    # Each connection is specified by options wr.network.connection
    conn_list=$(get_lxc_config_option_list "wr.network.connection" ${cfg_file})
    [ -z "${conn_list}" ] && return 0

    # Going through each connection
    for conn in ${conn_list}; do

        # Extract this connection specific info
        type=$(get_lxc_config_option "wr.network.${conn}.type" ${cfg_file})
        cn_pid=""

        # Going through each connection
        if [ "${type}" == "${NET_TYPE_VETH}" ]; then
            remote_cn=$(get_lxc_config_option "wr.network.${conn}.remote.cn" ${cfg_file})
            remote_type=$(get_lxc_config_option "wr.network.${conn}.remote.type" ${cfg_file})
            remote_link=$(get_lxc_config_option "wr.network.${conn}.remote.link" ${cfg_file})
            cn_eth_name=$(lxc_get_veth_cn_end_name ${conn} ${cn_name})
            remote_eth_name=$(lxc_get_veth_remote_end_name ${conn} ${cn_name})

            # veth pip has 2 ends: "cn end" and "remote end".  "cn end"
            # will be in being launching container net namespace, which we do not
            # care here.  "remote end" will be in net namespace of a container
            # (specified in config file) or of host.
            #
            # Here we will only configure "remote end" as other function
            # will configure "cn end".  Configure for "host" is a bit special, so
            # we need to handle it separately.

            if [ "${remote_cn}" == "${HOST_CN_NAME}" ]; then
                # Need to create veth from host net namespace
                if [ -d "${host_proc_path}/1" ]; then
                    cn_pid="${host_proc_path}/1"
                else
                    lxc_log "Error, host proc path ${host_proc_path} does not exist."
                    ret=1
                    break
                fi
                nsenter -n -t ${cn_pid} -- ip link add name ${cn_eth_name} type veth peer name ${remote_eth_name}
                if [ $? -ne 0 ]; then
                    lxc_log "Error, ${conn}, cannot create veth [${cn_eth_name}, ${remote_eth_name}] in host net namespace"
                    ret=1
                    break
                fi

                # Need to park the "cn end" into Domain0 net namespace so that other
                # net namespace can easily see it.
                nsenter -n -t ${cn_pid} -- ip link set ${cn_eth_name} netns 1
                if [ $? -ne 0 ]; then
                    lxc_log "Error, ${conn}, cannot move ${cn_eth_name} into ${cn_pid} net namespace"
                    nsenter -n -t ${cn_pid} -- ip link delete ${remote_eth_name}
                    [ $? -ne 0 ] && lxc_log "Error, ${conn}, cannot delete [${cn_eth_name}, ${remote_eth_name}] in host net namespace"
                    ret=1
                    break
                fi

            elif [ -n "${remote_cn}" ]; then
                cn_pid=$(get_lxc_init_pid_from_cn_name ${remote_cn})
                if [ -n "${cn_pid}" ]; then
                    # No need to invoke nsenter here as we want to park "cn end" into Domain0
                    # net namespace, and at this point we are actually in Domain0 net namespace.
                    ip link add name ${cn_eth_name} type veth peer name ${remote_eth_name}
                    if [ $? -ne 0 ]; then
                        lxc_log "Error, ${conn}, cannot create veth pipe in Domain0 net namespace"
                        ret=1
                        break
                    fi
                    ip link set ${remote_eth_name} netns ${cn_pid}
                    if [ $? -ne 0 ]; then
                        lxc_log "Error, ${conn}, cannot move ${remote_eth_name} into ${cn_pid} net namespace"
                        ip link delete ${cn_eth_name}
                        [ $? -ne 0 ] && lxc_log "Error, ${conn}, cannot delete [${cn_eth_name}, ${remote_eth_name}] in host net namespace"
                        ret=1
                        break
                    fi
                else
                    continue
                fi
            fi

            if [ -n "${cn_pid}" ]; then
                nsenter_netns_ext="nsenter -n -t ${cn_pid} --"
                remote_flags=$(get_lxc_config_option "wr.network.${conn}.remote.flags" ${cfg_file})

                hwaddr=$(get_lxc_config_option "wr.network.${conn}.remote.hwaddr" ${cfg_file})
                if [ -n "${hwaddr}" ]; then
                    ${nsenter_netns_ext} ip link set ${remote_eth_name} address ${hwaddr}
                    [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set hwaddrs ${hwaddr} to ${remote_eth_name}"
                fi

                case "${remote_type}" in
                    ovs)
                        # As openvswitch client controls ovs switch through sock under /var/run/openvswitch
                        # so its necessary to jump into mount namespace with option -m.  Also there are rare cases
                        # ovs get into bad state and this causes ovs-vsctl not to return, so use timeout here.  30s
                        # seems to be reasonable.
                        res=$(nsenter -m -n -t ${cn_pid} -- find /sys/class/net -name ${remote_link})
                        if [ -z "${res}" ];then
                            timeout 30 nsenter -m -n -t ${cn_pid} -- ovs-vsctl add-br ${remote_link}
                            [ $? -ne 0 ] && lxc_log "Warning, ${conn}, ovs-vsctl cannot create bridge ${remote_link}, res=$?"
                        fi
                        timeout 30 nsenter -m -n -t ${cn_pid} -- ovs-vsctl add-port ${remote_link} ${remote_eth_name}
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, ovs-vsctl cannot add ${remote_link} to ${remote_eth_name}, res=$?"
                        ;;
                    bridge)
                        res=$(nsenter -m -n -t ${cn_pid} -- find /sys/class/net -name ${remote_link})
                        if [ -z "${res}" ]; then
                            ${nsenter_netns_ext} brctl addbr ${remote_link}
                            [ $? -ne 0 ] && lxc_log "Warning, ${conn}, brctl cannot create bridge ${remote_link}, res=$?"
                        fi
                        ${nsenter_netns_ext} brctl addif ${remote_link} ${remote_eth_name}
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, brctl cannot add ${remote_link} to ${remote_eth_name}"
                        ;;
                    *)
                        # Here remote_link might be set inside config file.  At this point remote_link must be emtpy.
                        # Reset here as remote_link will be used later.
                        remote_link=""
                        ;;
                esac

                ipv4=$(get_lxc_config_option "wr.network.${conn}.remote.ipv4" ${cfg_file})
                if [ -n "${ipv4}" ]; then
                    if [ -n "${remote_link}" ]; then
                        ${nsenter_netns_ext} ip addr add ${ipv4} dev ${remote_link}
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set ipv4 ${ipv4} to ${remote_eth_name}"
                    else
                        ${nsenter_netns_ext} ip addr add ${ipv4} dev ${remote_eth_name}
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set ipv4 ${ipv4} to ${remote_eth_name}"
                    fi
                fi

                if [ "${remote_flags}" == "up" ]; then
                    ${nsenter_netns_ext} ip link set ${remote_eth_name} up
                    [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot activate ${remote_eth_name}"
                    if [ -n "${remote_link}" ]; then
                        ${nsenter_netns_ext} ip link set ${remote_link} up
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot activate ${remote_link}"
                    fi
                fi

                # Up script will be executed in Domain0 all namespaces.  Its up to the script
                # to decide different namespace to jump into.
                script_up=$(get_lxc_config_option "wr.network.${conn}.remote.script.up" ${cfg_file})
                if [ -x "${script_up}" ]; then
                    ${script_up} "${type}" "${cn_name}" "${remote_cn}" "${cn_pid}" "${remote_type}" "${remote_link}" "${remote_eth_name}"
                elif [ -n "${script_up}" ]; then
                    lxc_log "Warning, up script ${script_up} is not executed.  Make sure it is executable"
                fi
            fi
        fi
        if [ "${type}" == "${NET_TYPE_DPDK}" ]; then
            # Set the number of hugepages.
            if [ -n "${DPDK_HUGEFS_NR_HUGEPAGES}" ]; then
                echo ${DPDK_HUGEFS_NR_HUGEPAGES} > /sys/kernel/mm/hugepages/hugepages-2048kB/nr_hugepages
            fi

            # Prepare hugepage filesystem used for sharing between containers.
            if [ -n "${DPDK_HUGEFS_HOST_PATH_DEFAULT}" ]; then
                res=$(cat /proc/mounts | grep -F "${DPDK_HUGEFS_HOST_PATH_DEFAULT}" | awk '{print $3}')
                if [ "${res}" != "hugetlbfs" ]; then
                    mount -t hugetlbfs none ${DPDK_HUGEFS_HOST_PATH_DEFAULT}
                fi
            fi

            # Get list of kernel modules required to be loaded
            dpdk_kernmod_list=$(get_lxc_config_option "wr.network.${conn}.dpdk.kernmod" ${cfg_file} | sed 's/;/ /g')
            for kermod in ${dpdk_kernmod_list}; do
                # Security, only load module that contains "uio" substring
                # except for rte_kni.
                if [ "${kermod}" != "rte_kni" ]; then
                    res=$(echo ${kermod} | grep -F "uio")
                    [ -z "${res}" ] && break
                fi

                res=$(cat /proc/modules | awk '{print $1}' | grep "^${kermod}")
                if [ -z "${res}" ]; then
                    /sbin/modprobe ${kermod} || { ret=1; break; }
                fi
            done

            dpdk_pci_list=$(get_lxc_config_option "wr.network.${conn}.dpdk.pci" ${cfg_file} | sed 's/;/ /g')
            for pci_info in ${dpdk_pci_list}; do

                # pci_info has the following format:  <pci address>,<dpdk uio driver>
                # If driver is not provided then use ${DPDK_DEFAULT_PCI_DRIVER} as default.

                pci=$(echo ${pci_info} | awk -F ',' '{print $1}')
                [ -z "${pci}" ] && continue
                driver=$(echo ${pci_info} | awk -F ',' '{print $2}')
                # If driver is not provided then use ${DPDK_DEFAULT_PCI_DRIVER} as default
                [ -z "${driver}" ] && driver=${DPDK_DEFAULT_PCI_DRIVER}

                # If well known ${DPDK_DEFAULT_PCI_DRIVER} driver is required then make sure
                # ${DPDK_DEFAULT_PCI_DRIVER} module is load. This module might already explicitly
                # specified in dpdk_kernmod_list above.  But load it here any way
                if [ "${driver}" == "${DPDK_DEFAULT_PCI_DRIVER}" ]; then
                    res=$(cat /proc/modules | awk '{print $1}' | grep "^${DPDK_DEFAULT_PCI_DRIVER}")
                    if [ -z "${res}" ]; then
                        /sbin/modprobe ${DPDK_DEFAULT_PCI_DRIVER} || { ret=1; break; }
                    fi
                fi
                # Make sure that this pci device is not used by any other driver
                if [ -e "/sys/bus/pci/devices/${pci}/driver/" -a ! -e "/sys/bus/pci/devices/${pci}/net/" ]; then
                    lxc_log "Error, pci devide ${pci} is currently being used"
                    ret=1
                    break
                fi

            res=$($(dirname ${0})/dpdk_nic_bind_wrapper.py ${driver} -b ${driver} ${pci} 2>&1)
            [ -n "${res}" ] && { ret=1; break; }
            done

            # Up script will be executed in Domain0 all namespaces.  Its up to the script
            # to decide different namespace to jump into.
            script_up=$(get_lxc_config_option "wr.network.${conn}.remote.script.up" ${cfg_file})
            if [ -x "${script_up}" ]; then
                ${script_up} "${type}" "${cn_name}" "${dpdk_pci_list}" "${dpdk_kernmod_list}"
            elif [ -n "${script_up}" ]; then
                lxc_log "Warning, up script ${script_up} is not executed.  Make sure it is executable"
            fi
        fi
    done

    # Some connections failed creation, so cleanup
    if [ ${ret} -ne 0 ]; then
        lxc_log "Error, cannot setup network ${conn}"
        lxc_conn_setup_failed=${conn}
        lxc_remove_net ${cfg_file} ${cn_name}
        return 1
    fi
    return 0
}

function lxc_remove_net {
    local cfg_file=${1}
    local cn_name=${2}

    # Some basic checks
    [ ! -e "${cfg_file}" ] && return 1
    which ip > /dev/null 2>&1
    [ $? -ne 0 ] && lxc_log "Error, ip util is not available" && return 1

    # Get list of connections specified in cfg file.
    # Each connection is specified by options wr.network.connection
    conn_list=$(get_lxc_config_option_list "wr.network.connection" ${cfg_file})
    [ -z "${conn_list}" ] && return 1

    # Going through each connection
    for conn in ${conn_list}; do

        # Extract this connection specific info
        type=$(get_lxc_config_option "wr.network.${conn}.type" ${cfg_file})
        cn_pid=""

        if [ "${type}" == "${NET_TYPE_VETH}" ]; then
            remote_cn=$(get_lxc_config_option "wr.network.${conn}.remote.cn" ${cfg_file})
            remote_type=$(get_lxc_config_option "wr.network.${conn}.remote.type" ${cfg_file})
            remote_link=$(get_lxc_config_option "wr.network.${conn}.remote.link" ${cfg_file})
            remote_eth_name="veth-${cn_name}-${conn}-1"

            # Get pid path of correct net namespace
            if [ "${remote_cn}" == "${HOST_CN_NAME}" ]; then
                if [ -d "${host_proc_path}/1" ]; then
                    cn_pid="${host_proc_path}/1"
                else
                    lxc_log "Error, host proc path ${host_proc_path} does not exist."
                    return 1
                fi
            elif [ -n "${remote_cn}" ]; then
                cn_pid=$(get_lxc_init_pid_from_cn_name ${remote_cn})
            fi

            if [ -n "${cn_pid}" ]; then
                # Down script will be executed in Domain 0 all namespaces.  Its up to the script
                # to decide what namespace to jump into
                script_down=$(get_lxc_config_option "wr.network.${conn}.remote.script.down" ${cfg_file})
                if [ -x "${script_down}" ]; then
                    ${script_down} "${type}" "${cn_name}" "${remote_cn}" "${cn_pid}" "${remote_type}" "${remote_link}" "${remote_eth_name}"
                elif [ -n "${script_down}" ]; then
                    lxc_log "Warning, down script ${script_down} is not executed.  Make sure it is executable"
                fi

                # Clean up and bridge that has "remote end" attached to.
                case "${remote_type}" in
                    ovs)
                        # As openvswitch client control ovs switch through sock under /var/run/openvswitch
                        # so its necessary to jump into mount namespace with option -m. Also there are rare cases
                        # ovs get into bad state, this causes ovs-vsctl to not return, so use timeout here.  30s
                        # seem to be reasonable.
                        timeout 30 nsenter -m -n -t ${cn_pid} -- ovs-vsctl del-port ${remote_link} ${remote_eth_name}
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, ovs-vsctl cannot delete ${remote_link} outof ${remote_eth_name}, res=$?"
                        ;;
                    bridge)
                        nsenter -n -t ${cn_pid} -- brctl delif ${remote_link} ${remote_eth_name}
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, brctl cannot delete ${remote_link} outof ${remote_eth_name}"
                        ;;
                    *)
                        ;;
                esac
                # Now delete the veth pipe.  The "cn end" will disapper after this call as well.
                nsenter -n -t ${cn_pid} -- ip link delete ${remote_eth_name}
                [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot remove ${remote_eth_name}"
            else
                lxc_log "ERROR. cannot remove net specified by ${cfg_file} because cannot"
                lxc_log "find pid namespace of container ${remote_cn}"
                return 1
            fi
        fi
        if [ "${type}" == "${NET_TYPE_DPDK}" ]; then
            dpdk_pci_list=$(get_lxc_config_option "wr.network.${conn}.dpdk.pci" ${cfg_file} | sed 's/;/ /g')
            dpdk_kernmod_list=$(get_lxc_config_option "wr.network.${conn}.dpdk.kernmod" ${cfg_file} | sed 's/;/ /g')
            cn_pid=$(get_lxc_init_pid_from_cn_name ${cn_name})
            nsenter_ext=""
            [ -n "${cn_pid}" ] && nsenter_ext="nsenter -n -m -p -t ${cn_pid} --"

            # Down script will be executed in Domain 0 all namespaces.  Its up to the script
            # to decide what namespace to jump into
            script_down=$(get_lxc_config_option "wr.network.${conn}.remote.script.down" ${cfg_file})
            if [ -x "${script_down}" ]; then
                ${script_down} "${type}" "${cn_name}" "${dpdk_pci_list}" "${dpdk_kernmod_list}"
            elif [ -n "${script_down}" ]; then
                lxc_log "Warning, down script ${script_down} is not executed.  Make sure it is executable"
            fi

            if [ -n "${cn_pid}" ]; then
                dpdk_hugefs_path=$(get_lxc_config_option "wr.network.${conn}.dpdk.hugefs.path" ${cfg_file})
                [ -z "${dpdk_hugefs_path}" ] && dpdk_hugefs_path=${DPDK_HUGEFS_PATH_DEFAULT}
                ${nsenter_ext} umount ${dpdk_hugefs_path}
            fi

            for pci_info in ${dpdk_pci_list}; do
                # pci_info has the following format:  <pci address>,<dpdk uio driver>
                pci=$(echo ${pci_info} | awk -F ',' '{print $1}')
                [ -z "${pci}" ] && continue
                driver=$(echo ${pci_info} | awk -F ',' '{print $2}')
                # If driver is not provided then use ${DPDK_DEFAULT_PCI_DRIVER} as default
                [ -z "${driver}" ] && driver=${DPDK_DEFAULT_PCI_DRIVER}

                if [ -n "${cn_pid}" ]; then
                    # pci_info has the following format:  <pci address>,<dpdk uio driver>
                    pci=$(${nsenter_ext} echo ${pci_info} | awk -F ',' '{print $1}')
                    sys_pci_uio_path=$(${nsenter_ext} find ${root_fs_mount}/sys/ -name "*uio*" | grep -F "${pci}" | grep "uio\/uio")
                    ${nsenter_ext} rm /dev/$(basename ${sys_pci_uio_path}) > /dev/null 2>&1
                fi

                echo ${pci} >> /sys/bus/pci/drivers/${driver}/unbind
                [ $? -ne 0 ] && lxc_log "Warning, dpdk cannot unbind ${pci} from driver ${driver}"
            done

            # Some dpdk kernel modules caused dev nodes to be created.  Remove them here.
            # Right now only dpk rte_kni requires /dev/kni to be removed
            res=$(echo ${dpdk_kernmod_list} | grep -F 'rte_kni')
            [ -n "${res}" ] && ${nsenter_ext} rm /dev/kni > /dev/null 2>&1

            # If this dpdk connection failed to setup then there is no need to cleanup
            # the rest of connections.
            [ "${lxc_conn_setup_failed}" == "${conn}" ] && break
        fi
   done
   return 0
}

# Set lxc.hook.pre-mount to Domain0 WindRiver lxc specific net hook in
# provided lxc container config file.  When this lxc container is
# launched this hook will setup additional networking specified within
# config file.
function lxc_add_net_hook_info_cfg {
    local cfg_file=${1}

    hook_script_path=$(dirname ${0})

    res=$(cat ${cfg_file} | sed 's/[ ,\t]//g' | grep "^lxc.hook.pre-mount=${hook_script_path}/lxc_hook_net_pre-mount.sh")
    if [ -z "${res}" ]; then
        echo >> ${cfg_file}
        echo "#################################################" >> ${cfg_file}
        echo "### Start WindRiver lxc net specific section ####" >> ${cfg_file}
        echo "lxc.hook.pre-mount = ${hook_script_path}/lxc_hook_net_pre-mount.sh" >> ${cfg_file}

        # If there is a dpdk type connection then we need to modify
        # cgroup to allow uio dev
        conn_list=$(get_lxc_config_option_list "wr.network.connection" ${cfg_file})
        for conn in ${conn_list}; do
            type=$(get_lxc_config_option "wr.network.${conn}.type" ${cfg_file})
            if [ "${type}" == "${NET_TYPE_DPDK}" ]; then
                echo "lxc.hook.mount = ${hook_script_path}/lxc_hook_net_mount.sh" >> ${cfg_file}
                echo "lxc.cgroup.devices.allow = c 249:* rwm" >> ${cfg_file}
                break
            fi
        done

        echo "### End WindRiver lxc net specific section   ####" >> ${cfg_file}
        echo "#################################################" >> ${cfg_file}
        echo >> ${cfg_file}
    fi
}
