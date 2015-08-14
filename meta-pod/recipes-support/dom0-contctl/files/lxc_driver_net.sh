#!/bin/bash

source `dirname $0`/lxc_common_helpers.sh

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

    conn_list=`get_lxc_config_option "wr.network.connection" ${cfg_file}`
    [ -z "${conn_list}" ] && return 1

    nsenter_ext=""
    [ -n "${cn_init_pid}" ] && nsenter_ext="nsenter -n -t ${cn_init_pid} --"

    # Going through each connection, at this point,
    # connection has been setup for "remote end" veth
    for conn in ${conn_list}; do
        cn_eth_name=`lxc_get_veth_cn_end_name ${conn} ${cn_name}`

        hwaddr=`get_lxc_config_option "wr.network.${conn}.hwaddr" ${cfg_file}`
        if [ -n "${hwaddr}" ]; then
            ${nsenter_ext} ip link set ${cn_eth_name} address ${hwaddr}
            [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set hwaddrs ${hwaddr} to ${cn_eth_name}"
        fi
        ipv4=`get_lxc_config_option "wr.network.${conn}.ipv4" ${cfg_file}`
        if [ -n "${ipv4}" ]; then
            ${nsenter_ext} ip addr add ${ipv4} dev ${cn_eth_name}
            [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set ipv4 ${ipv4} to ${cn_eth_name}"
        fi
        new_name=`get_lxc_config_option "wr.network.${conn}.name" ${cfg_file}`
        if [ -n "${new_name}" ]; then
            ${nsenter_ext} ip link set ${cn_eth_name} name ${new_name}
            if [ $? -eq 0 ]; then
                cn_eth_name=${new_name}
            else
                lxc_log "Warning, ${conn}, cannot change name from ${cn_eth_name} to ${new_name}"
            fi
        fi
        flags=`get_lxc_config_option "wr.network.${conn}.flags" ${cfg_file} | grep 'up'`
        if [ -n "${flags}" ]; then
            ${nsenter_ext} ip link set ${cn_eth_name} up
            [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot activate ${cn_eth_name}"
        fi
    done
}

function lxc_setup_net_cn_end {
    local cfg_file=${1}
    local cn_name=${2}

    # Some basic checks
    [ ! -e "${cfg_file}" ] && return 1
    cn_init_pid=`get_lxc_init_pid_from_cn_name ${cn_name}`
    [ -z "${cn_init_pid}" ] && return 1
    which ip > /dev/null 2>&1
    [ $? -ne 0 ] && lxc_log "Error, ip util is not available" && return 1

    conn_list=`get_lxc_config_option "wr.network.connection" ${cfg_file}`
    [ -z "${conn_list}" ] && return 1

    # Going through each connection, at this point,
    # connection has been setup for "remote end" veth
    for conn in ${conn_list}; do
        cn_eth_name=`lxc_get_veth_cn_end_name ${conn} ${cn_name}`
        # Now switch "cn end" to correct namespace
        ip link set ${cn_eth_name} netns ${cn_init_pid}
        [ $? -ne 0 ] && lxc_log "Error, ${conn}, cannot switch ${cn_eth_name} to ${cn_init_pid} net namespace" \
                && return 1
    done
    lxc_set_net_cn_end_options ${cfg_file} ${cn_name} ${cn_init_pid}

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
    conn_list=`get_lxc_config_option "wr.network.connection" ${cfg_file}`
    [ -z "${conn_list}" ] && return 0

    # Going through each connection
    for conn in ${conn_list}; do

        # Extract this connection specific info
        type=`get_lxc_config_option "wr.network.${conn}.type" ${cfg_file}`
        remote_cn=`get_lxc_config_option "wr.network.${conn}.remote.cn" ${cfg_file}`
        remote_type=`get_lxc_config_option "wr.network.${conn}.remote.type" ${cfg_file}`
        remote_link=`get_lxc_config_option "wr.network.${conn}.remote.link" ${cfg_file}`
        cn_eth_name="veth-${cn_name}-${conn}-0"
        remote_eth_name="veth-${cn_name}-${conn}-1"
        cn_pid=""

        # Going through each connection
        if [ "${type}" == "veth" ]; then

            # veth pip has 2 ends: "cn end" and "remote end".  "cn end"
            # will be in being launching container net namespace, which we do not
            # care here.  "remote end" will be in net namespace of a container
            # (specified in config file) or of host.
            #
            # Here we will only configure "remote end" as other function
            # will configure "cn end".  Configure for "host" is a bit special, so
            # we need to handle it separately.

            if [ "${remote_cn}" == "host" ]; then
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
                cn_pid=`get_lxc_init_pid_from_cn_name ${remote_cn}`
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
                hwaddr=`get_lxc_config_option "wr.network.${conn}.remote.hwaddr" ${cfg_file}`
                if [ -n "${hwaddr}" ]; then
                    nsenter -n -t ${cn_pid} -- ip link set ${remote_eth_name} address ${hwaddr}
                    [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set hwaddrs ${hwaddr} to ${remote_eth_name}"
                fi
                ipv4=`get_lxc_config_option "wr.network.${conn}.remote.ipv4" ${cfg_file}`
                if [ -n "${ipv4}" ]; then
                    nsenter -n -t ${cn_pid} -- ip addr add ${ipv4} dev ${remote_eth_name}
                    [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot set ipv4 ${ipv4} to ${remote_eth_name}"
                fi

                case "${remote_type}" in
                    ovs)
                        # As openvswitch client controls ovs switch through sock under /var/run/openvswitch
                        # so its neccessary to jump into mount namespace with option -m.  Also there are rare cases
                        # ovs get into bad state and this causes ovs-vsctl not to return, so use timeout here.  30s
                        # seems to be reasonable.
                        timeout 30 nsenter -m -n -t ${cn_pid} -- ovs-vsctl add-port ${remote_link} ${remote_eth_name}
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, ovs-vsctl cannot add ${remote_link} to ${remote_eth_name}, res=$?"
                        ;;
                    bridge)
                        nsenter -n -t ${cn_pid} -- brctl addif ${remote_link} ${remote_eth_name}
                        [ $? -ne 0 ] && lxc_log "Warning, ${conn}, brctl cannot add ${remote_link} to ${remote_eth_name}"
                        ;;
                    *)
                        ;;
                esac
                flags=`get_lxc_config_option "wr.network.${conn}.remote.flags" ${cfg_file} | grep 'up'`
                if [ -n "${flags}" ]; then
                    nsenter -n -t ${cn_pid} -- ip link set ${remote_eth_name} up
                    [ $? -ne 0 ] && lxc_log "Warning, ${conn}, cannot activate ${remote_eth_name}"
                fi

                # Up script will be executed in Domain0 all namespaces.  Its up to the script
                # to decide different namespace to jump into.
                script_up=`get_lxc_config_option "wr.network.${conn}.remote.script.up" ${cfg_file}`
                if [ -x "${script_up}" ]; then
                    ${script_up} "${remote_cn}" "${cn_pid}" "${type}" "${remote_type}" "${remote_link}"
                else
                    lxc_log "Warning, up script ${script_up} is not executed.  Make sure it is executable"
                fi
            fi
        fi
    done

    # Some connections are failed to created, so cleanup
    if [ ${ret} -ne 0 ]; then
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
    conn_list=`get_lxc_config_option "wr.network.connection" ${cfg_file}`
    [ -z "${conn_list}" ] && return 1

    # Going through each connection
    for conn in ${conn_list}; do

        # Extract this connection specific info
        type=`get_lxc_config_option "wr.network.${conn}.type" ${cfg_file}`
        remote_cn=`get_lxc_config_option "wr.network.${conn}.remote.cn" ${cfg_file}`
        remote_type=`get_lxc_config_option "wr.network.${conn}.remote.type" ${cfg_file}`
        remote_link=`get_lxc_config_option "wr.network.${conn}.remote.link" ${cfg_file}`
        remote_eth_name="veth-${cn_name}-${conn}-1"
        cn_pid=""

        if [ "${type}" == "veth" ]; then
            # Get pid path of correct net namespace
            if [ "${remote_cn}" == "host" ]; then
                if [ -d "${host_proc_path}/1" ]; then
                    cn_pid="${host_proc_path}/1"
                else
                    lxc_log "Error, host proc path ${host_proc_path} does not exist."
                    return 1
                fi
            elif [ -n "${remote_cn}" ]; then
                cn_pid=`get_lxc_init_pid_from_cn_name ${remote_cn}`
            fi

            if [ -n "${cn_pid}" ]; then
                # Down script will be executed in Domain 0 all namespaces.  Its up to the script
                # to decide what namespace to jump into
                script_down=`get_lxc_config_option "wr.network.${conn}.remote.script.down" ${cfg_file}`
                if [ -x "${script_down}" ]; then
                    ${script_down} "${remote_cn}" "${cn_pid}" "${type}" "${remote_type}" "${remote_link}"
                else
                    lxc_log "Warning, down script ${script_down} is not executed.  Make sure it is executable"
                fi

                # Clean up and bridge that has "remote end" attached to.
                case "${remote_type}" in
                    ovs)
                        # As openvswitch client control ovs switch through sock under /var/run/openvswitch
                        # so its neccessary to jump into mount namespace with option -m. Also there are rare cases
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
   done
   return 0
}

# Set lxc.hook.pre-mount to Domain0 WindRiver lxc specific net hook in
# provided lxc container config file.  When this lxc container is
# launched this hook will setup additional networking specified within
# config file.
function lxc_add_net_hook_info_cfg {
    local cfg_file=${1}

    hook_script_loc="`dirname $0`/lxc_hook_net_pre-mount.sh"

    res=`cat ${cfg_file} | sed 's/[ ,\t]//g' | grep "^lxc.hook.pre-mount=${hook_script_loc}"`
    if [ -z "${res}" ]; then
        echo >> ${cfg_file}
        echo "#################################################" >> ${cfg_file}
        echo "### Start WindRiver lxc net specific section ####" >> ${cfg_file}
        echo "lxc.hook.pre-mount = ${hook_script_loc}" >> ${cfg_file}
        echo "### End WindRiver lxc net specific section   ####" >> ${cfg_file}
        echo "#################################################" >> ${cfg_file}
        echo >> ${cfg_file}
    fi
}
