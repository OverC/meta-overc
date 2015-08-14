#! /bin/bash

source `dirname $0`/lxc_common_helpers.sh

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

function launch_peer_container {
    local cn_name=${1}
    local host_proc=${2}

    is_cn_exist ${cn_name}
    [ "$?" == "0" ] && echo "Error, container ${cn_name} does not exist." && return 1

    if [ -d "${host_proc_path}/1" ]; then
        nsenter -b ${host_proc_path}/1:/proc/1 --net --target ${host_proc_path}/1 \
                -- lxc-start -n ${cn_name} -d
        return 0
    else
        echo "ERROR: host proc path ${host_proc_path} does not exist."
        return 1
    fi
}

function launch_nested_container {
    local cn_name=${1}
    local parent_cn_name=${2}

    is_cn_exist ${cn_name}
    [ "$?" == "0" ] && echo "Error, container ${cn_name} does not exist." && return 1

    is_current_cn ${parent_cn_name}
    if [ "$?" == "1" ] ; then
        lxc-start -n ${cn_name} -d
    else
        lxc_init_pid=`get_lxc_init_pid_from_cn_name ${parent_cn_name}`
        if [ -n "${lxc_init_pid}" ]; then
            nsenter -b /proc/${lxc_init_pid}:/proc/1 --net --target ${lxc_init_pid} -- \
                    lxc-start -n ${cn_name} -d
            return 0
        else
            echo "Cannot launch container ${cn_name}."
            echo "Parent container ${parent_cn_name} is not active."
            return 1
        fi
    fi
}

function enter_container_ns {
    local cn_name=${1}

    is_cn_exist ${cn_name}
    [ "$?" == "0" ] && echo "Error, container ${cn_name} does not exist." && return 1

    lxc_init_pid=`get_lxc_init_pid_from_cn_name ${cn_name}`
    if [ -n "${lxc_init_pid}" ]; then
        nsenter --mount --uts --ipc --net --pid --target ${lxc_init_pid}
        return 0
    else
        echo "Cannot enter container ${cn_name}. Is it running?"
        return 1
    fi
}

function stop_container {
    local cn_name=${1}
    local child_name=""

    is_cn_exist ${cn_name}
    [ "$?" == "0" ] && echo "Error, container ${cn_name} does not exist." && return 1

    current_cn_name=`get_cn_name_from_init_pid 1`

    # Should not stop container while its childs is running.
    # So first find out if this container has any child.
    cn_list=`lxc-ls`
    for i in ${cn_list}; do
        # Dont count this stopping container or the container
        # this function is invoked.
        [ "${current_cn_name}" == "${i}" ] && continue
        [ "${cn_name}" == "${i}" ] && continue

        parent_cn_name=`get_parent_cn_name_from_cn_name ${i}`
        match_cn_name ${parent_cn_name} ${cn_name}
        if [ "$?" == "1" ]; then
            child_name="${i}"
            break
        fi
    done

    if [ -z ${child_name} ]; then
        exec_cmd_container ${cn_name} lxc-stop -n ${cn_name}
    else
        echo "Error, child ${child_name} container is running"
    fi
}

function list_containers {
    cn_list=`lxc-ls`
    current_cn_name=`get_cn_name_from_init_pid 1`

    # Just going through each of all existing containers
    # and find out its state
    for i in ${cn_list}; do
        if [ "${current_cn_name}" == "${i}" ]; then
            echo "${i} (lxc) - Container that I'm in"
            continue
        fi

        state=`get_lxc_cn_state_from_cn_name ${i}`
        if [ -n "${state}" ]; then
            parent_cn_name=`get_parent_cn_name_from_cn_name ${i}`
            echo "${i} (lxc) (${parent_cn_name}) - ${state}"
        else
            echo "${i} (lxc) - STOPPED"
        fi
    done
}
