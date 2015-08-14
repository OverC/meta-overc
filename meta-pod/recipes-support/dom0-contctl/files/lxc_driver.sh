#! /bin/bash

source $(dirname ${0})/lxc_common_helpers.sh
source $(dirname ${0})/lxc_driver_net.sh

function get_matching_container_group {
    local start_group=${1}

    # look in /var/lib/lxc/<container>/config for groups that match
    local lxcbase=${LXCBASE}
    if [ -z "${lxcbase}" ]; then
	lxcbase=/var/lib/lxc
    fi

    matches=""
    for c in `ls ${lxcbase}/`; do
	if [ -f "${lxcbase}/${c}/config" ]; then
	    group=`grep lxc.group "${lxcbase}/${c}/config" | cut -f2 -d'=' | sed 's/ *//'`
	    if [ "${group}" == "${start_group}" ]; then
		matches="${matches} ${c}"
	    fi
	fi
    done

    echo ${matches}
}

function lxc_launch_if_fail_then_clean_up {
    local cfg_file=${1}
    local cn_name=${2}

    # Check to see if cn is activated
    is_cn_running ${cn_name}
    if [ $? -eq 0 ]; then
        # If cn is not started then do some cleanup
        lxc_remove_net ${cfg_file} ${cn_name}
        lxc_log "Error, cannot launch container ${cn_name}."
    else
        # Its might be the case that the network options in cfg file
        # modified during container is up.  This will mess up the
        # networking clean up process later.  So save it.
        cp ${cfg_file} "$(dirname ${cfg_file})/.config-do-not-modify"
        [ $? -ne 0 ] && log_lxc "Warning, cannot save ${cfg_file}"
    fi
}

function lxc_launch_prepare {
    local cn_name=${1}

    # Make sure container is not running.
    is_cn_exist ${cn_name}
    [ $? -eq 0 ] && lxc_log "Error, container ${cn_name} does not exist." && return 1
    is_cn_running ${cn_name}
    [ $? -eq 1 ] && lxc_log "Error, container ${cn_name} is already running." && return 1

    # Setup networking
    cfg_file=$(get_lxc_default_config_file ${cn_name})
    lxc_add_net_hook_info_cfg ${cfg_file}
    lxc_setup_net_remote_end ${cfg_file} ${cn_name}
    if [ $? -ne 0 ]; then
        lxc_log "Error, cannot start ${cn_name} container."
        lxc_log "       Some network connections cannot be prepared."
        return 1
    fi
}

function save_bind_mount_proc_1 {
    local cn_name=${1}

    # Some lxc-hook scripts need to be able to jump back to Domain0 namespace.
    # As we will invoke nsenter with bind mount current /proc/1.  So let save
    # Domain0 proc 1 to another location.
    ctl_dom_proc_1_bind_mount_path=$(get_lxc_ctl_dom_proc_1_bind_mount_path ${cn_name})
    mkdir -p ${ctl_dom_proc_1_bind_mount_path} > /dev/null 2>&1
    mount -o bind /proc/1 ${ctl_dom_proc_1_bind_mount_path}
    [ $? -ne 0 ] && lxc_log "Error, cannot bind mount /proc/1 to ${ctl_dom_proc_1_bind_mount_path}"
}

function launch_peer_container {
    local cn_name=${1}

    lxc_launch_prepare ${cn_name}
    [ $? -ne 0 ] && return 1

    if [ -d "${host_proc_path}/1" ]; then
        save_bind_mount_proc_1 ${cn_name}
        nsenter -b ${host_proc_path}/1:/proc/1 --net --target ${host_proc_path}/1 \
                -- lxc-start -n ${cn_name} -d
        umount ${ctl_dom_proc_1_bind_mount_path}
        [ $? -ne 0 ] && lxc_log "Warning, cannot unmount ${ctl_dom_proc_1_bind_mount_path}." && return 1
    else
        lxc_log "ERROR: host proc path ${host_proc_path} does not exist."
        return 1
    fi

    lxc_launch_if_fail_then_clean_up ${cfg_file} ${cn_name}
}

function launch_nested_container {
    local cn_name=${1}
    local parent_cn_name=${2}

    lxc_launch_prepare ${cn_name}
    [ $? -ne 0 ] && return 1

    save_bind_mount_proc_1 ${cn_name}
    is_current_cn ${parent_cn_name}
    if [ "$?" == "1" ] ; then
        lxc-start -n ${cn_name} -d
    else
        lxc_init_pid=$(get_lxc_init_pid_from_cn_name ${parent_cn_name})
        if [ -n "${lxc_init_pid}" ]; then
            nsenter -b /proc/${lxc_init_pid}:/proc/1 --net --target ${lxc_init_pid} -- \
                    lxc-start -n ${cn_name} -d
        else
            lxc_log "Error, cannot launch container ${cn_name}. Parent container ${parent_cn_name} is not active."
            umount ${ctl_dom_proc_1_bind_mount_path}
            [ $? -ne 0 ] && lxc_log "Warning, cannot unmount ${ctl_dom_proc_1_bind_mount_path}."
            return 1
        fi
    fi

    umount ${ctl_dom_proc_1_bind_mount_path}
    [ $? -ne 0 ] && lxc_log "Warning, cannot unmount ${ctl_dom_proc_1_bind_mount_path}."
    lxc_launch_if_fail_then_clean_up ${cfg_file} ${cn_name}
}

function enter_container_ns {
    local cn_name=${1}

    is_cn_running ${cn_name}
    [ $? -eq 0 ] && lxc_log "Error, container ${cn_name} is not active." && return 1

    lxc_init_pid=$(get_lxc_init_pid_from_cn_name ${cn_name})
    if [ -n "${lxc_init_pid}" ]; then
        nsenter --mount --uts --ipc --net --pid --target ${lxc_init_pid}
        return 0
    else
        lxc_log "Error, cannot enter container ${cn_name}. Please make sure its active?"
        return 1
    fi
}

function stop_container {
    local cn_name=${1}
    local child_name=""

    is_cn_running ${cn_name}
    [ $? -eq 0 ] && lxc_log "Error, container ${cn_name} does not exist or not run." && return 1

    # When stopping container, we will use the saved config file to
    # do clean up especially for network.
    cfg_path="$(get_lxc_config_path)/${cn_name}"
    cfg_file="${cfg_path}/.config-do-not-modify"
    if [ ! -f "${cfg_file}" ]; then
        lxc_log "Fatal Error, saved file ${cfg_path}/.config-do-not-modify does not exist?"
        return 1;
    fi

    current_cn_name=$(get_cn_name_from_init_pid 1)

    # Should not stop container while its childs is running.
    # So first find out if this container has any child.
    cn_list=$(lxc-ls)
    for i in ${cn_list}; do
        # Dont count this stopping container or the container
        # this function is invoked.
        [ "${current_cn_name}" == "${i}" ] && continue
        [ "${cn_name}" == "${i}" ] && continue

        parent_cn_name=$(get_parent_cn_name_from_cn_name ${i})
        match_cn_name ${parent_cn_name} ${cn_name}
        if [ "$?" == "1" ]; then
            child_name="${i}"
            break
        fi
    done

    if [ -z ${child_name} ]; then
        exec_lxc_cmd_cn ${cn_name} lxc-stop -n ${cn_name}
    else
        lxc_log "Error, child ${child_name} container is running"
    fi

    # Now clean up all the "remote end"
    lxc_remove_net ${cfg_file} ${cn_name}
    rm ${cfg_file} > /dev/null 2>&1
}

function list_containers {
    cn_list=$(lxc-ls)
    current_cn_name=$(get_cn_name_from_init_pid 1)

    # Just going through each of all existing containers
    # and find out its state
    for i in ${cn_list}; do
        if [ "${current_cn_name}" == "${i}" ]; then
            echo "${i} (lxc) - Container that I'm in"
            continue
        fi

        state=$(get_lxc_cn_state_from_cn_name ${i})
        if [ -n "${state}" ]; then
            parent_cn_name=$(get_parent_cn_name_from_cn_name ${i})
            echo "${i} (lxc) (${parent_cn_name}) - ${state}"
        else
            echo "${i} (lxc) - STOPPED"
        fi
    done
}

function setup_net {
    local cn_name=${1}
    local cfg_file=${2}

    lxc_setup_net_remote_end ${cfg_file} ${cn_name}
    if [ $? -ne 0 ]; then
        lxc_log "Error, some network connections cannot be prepared."
        return 1
    fi
    lxc_setup_net_cn_end ${cfg_file} ${cn_name}
}

function remove_net {
    local cn_name=${1}
    local cfg_file=${2}

    lxc_remove_net ${cfg_file} ${cn_name}
}
