#! /bin/bash

CGROUP_NAME="cpuset"

# Sometime dom0_contctl is invoked without user env passed in (invoked under
# systemd).  The script relies on PATH env for invoking various utilities.
# So if PATH env is not provided then export one.
res=$(/usr/bin/env | grep "^PATH=")
[ -z "${res}" ] && export PATH="/usr/local/bin:/usr/bin:/bin:/usr/local/sbin:/usr/sbin:/sbin"

function lxc_log {
    local msg=${1}

    echo ${msg}
}

function match_cn_name {

    # If a container is not through lxc-stop
    # command then next time this container started
    # will have some suffix added the real container
    # name.  This helper function makes sure the container
    # names match.

    [ -z "${1}" -o -z "${2}" ] && return 0
    match1=$(echo ${1} | grep "^${2}")
    match2=$(echo ${2} | grep "^${1}")
    [ -n "${match1}" -o -n "${match2}" ] && return 1
    return 0
}

function get_cn_name_from_init_pid {
    local init_pid=${1}

    cn_name=$(cat /proc/${init_pid}/cgroup | grep ":${CGROUP_NAME}:" |\
            cut -d ":" -f 3 | xargs basename)
    echo "${cn_name}"
}

function get_lxc_mgr_pid_from_cn_name {
    local cn_name=${1}

    # lxc-start process is the main controller
    # of lxc container.  All lxc-xxxx commands must
    # inherit net namespace of this process.

    lxc_mgr_pid_list=$(ps -e -o comm,pid | grep "^lxc-start" | awk '{print $2}')
    for i in ${lxc_mgr_pid_list}; do
        lxc_cn_init_pid=$(pgrep -P $i)
        this_cn_name=$(get_cn_name_from_init_pid ${lxc_cn_init_pid})

        match_cn_name ${this_cn_name} ${cn_name}
        if [ $? -eq 1 ]; then
            echo "${i}"
            return 0
        fi
    done
    echo ""
    return 1
}

function get_lxc_init_pid_from_cn_name {
    local cn_name=${1}

    lxc_init_pid=""

    # Each of lxc-start process has exactly
    # 1 child process which is the first process
    # (normally /sbin/init") of container

    lxc_mgr_pid=$(get_lxc_mgr_pid_from_cn_name ${cn_name})
    if [ -n "${lxc_mgr_pid}" ]; then
        lxc_init_pid=$(pgrep -P ${lxc_mgr_pid})
    fi
    echo ${lxc_init_pid}
}

function get_parent_cn_name_from_cn_name {
    local cn_name=${1}

    parent_cn_name=""
    lxc_init_pid=$(get_lxc_init_pid_from_cn_name ${cn_name})
    if [ -n "${lxc_init_pid}" ]; then
        parent_cn_name=$(cat /proc/${lxc_init_pid}/cgroup | grep ":${CGROUP_NAME}:" | \
                cut -d ":" -f 3 | sed 's:/lxc/:/:g' | xargs dirname | xargs basename)
    fi
    [ "${parent_cn_name}" == "/" ] && parent_cn_name=${HOST_CN_NAME}

    echo ${parent_cn_name}
}

function is_cn_exist {
    local cn_name=${1}

    cn_list=$(lxc-ls)
    for i in ${cn_list}; do
        [ "${i}" == "${cn_name}" ] && return 1
    done
    return 0
}

function is_current_cn {
    local cn_name=${1}

    current_cn_name=$(get_cn_name_from_init_pid 1)
    [ "${current_cn_name}" == "${cn_name}" ] && return 1
    return 0
}

function exec_cmd_container {
    local cn_name=${1}
    shift

    is_cn_exist ${cn_name}
    [ $? -eq 0 ] && lxc_log "Error, container ${cn_name} does not exist." && return 1

    is_current_cn ${cn_name}
    if [ $? -eq 1 ] ; then
        $@
    else
        lxc_init_pid=$(get_lxc_init_pid_from_cn_name ${cn_name})
        if [ -n "${lxc_init_pid}" ]; then
            are_pids_same_namespace "user" "1" "${lxc_init_pid}"
            [ $? -ne 1 ] && nsenter_opts="-U"
            nsenter ${nsenter_opts} -u -i -m -n -p -t ${lxc_init_pid} -- $@
        else
	    # if we can't find the init process, we hed to the host, since this
	    # is a peer container
	    do_essential_cmd lxc-attach -n ${cn_name} $@
        fi
    fi
}

function exec_lxc_cmd_cn {
    local cn_name=${1}
    shift

    if [ -z "${cn_name}" ]; then
        cn_name=$(echo $@ | awk -F '-n' '{print $2}' | awk -F ' ' '{print $1}')
    fi

    is_cn_exist ${cn_name}
    [ $? -eq 0 -a "${cn_name}" != "${HOST_CN_NAME}" ] && lxc_log "Error, container ${cn_name} does not exist." && return 1

    is_current_cn ${cn_name}
    if [ $? -eq 1 ] ; then
        $@
    elif [ "${cn_name}" == "${HOST_CN_NAME}" ]; then
        # nsenter -n -t ${host_proc_path}/1 -- $@
	do_essential_cmd $@
	return $?
    else
	# if there's no init process, we head down to essential
        lxc_mgr_pid=$(get_lxc_mgr_pid_from_cn_name ${cn_name})
        if [ -n "${lxc_mgr_pid}" ]; then
            nsenter --net --target ${lxc_mgr_pid} -- $@
        else
	    do_essential_cmd $@
            return $?
        fi
    fi
}

function get_lxc_cn_state_from_cn_name {
    local cn_name=${1}

    state=$(exec_lxc_cmd_cn ${cn_name} lxc-info -n ${cn_name} \
            | grep "State:" | awk '{print $2}')
    echo "${state}"
}

function is_cn_running {
    local cn_name=${1}

    state=$(get_lxc_cn_state_from_cn_name ${cn_name})
    if [ "${state}" == "RUNNING" ]; then
        return 1
    else
        return 0
   fi
}

function is_cn_stopped {
    local cn_name=${1}

    state=$(get_lxc_cn_state_from_cn_name ${cn_name})
    if [ -z "${state}" -o "${state}" == "STOPPED" ]; then
        return 1
    else
        return 0
   fi
}

function get_lxc_config_path {
    echo $(lxc-config lxc.lxcpath)
}

function get_lxc_default_config_file {
    local cn_name=${1}

    echo "$(get_lxc_config_path)/${cn_name}/config"
}

MOD_CONFIG_FILE_NAME=".config-modified-do-not-touch"
ORIG_CONFIG_FILE_NAME=".config-original-do-not-touch"
DB_FILE_NAME=".lxc-keys-db-do-not-touch"

function get_lxc_mod_config_file {
    local cn_name=${1}

    echo "$(get_lxc_config_path)/${cn_name}/${MOD_CONFIG_FILE_NAME}"
}

function save_orig_config_file {
    local orig_cfg_file=${1}

    cp ${orig_cfg_file} "$(dirname ${orig_cfg_file})/${ORIG_CONFIG_FILE_NAME}"
    [ $? -ne 0 ] && log_lxc "Warning, cannot save mod config file ${orig_cfg_file}"
}

function save_mod_config_file {
    local mod_cfg_file=${1}

    cp ${mod_cfg_file} "$(dirname ${mod_cfg_file})/${MOD_CONFIG_FILE_NAME}"
    [ $? -ne 0 ] && log_lxc "Warning, cannot save mod config file ${mod_cfg_file}"
}

function write_key_db {
    local cn_name=${1}
    local key=${2}
    local value=${3}
    local db_file="$(get_lxc_config_path)/${cn_name}/${DB_FILE_NAME}"

    [ ! -e "${db_file}" ] && touch ${db_file}
    sed -i "/${key}/d" ${db_file}
    echo "${key}=${value}" >> ${db_file}
}

function get_key_db {
    local cn_name=${1}
    local key=${2}
    local db_file="$(get_lxc_config_path)/${cn_name}/${DB_FILE_NAME}"

    if [ -e "${db_file}" ]; then
        cat ${db_file} | grep "^${key}" | cut -d '=' -f 2
    fi
}

function restore_all_config_files {
    local orig_cfg_file=${1}

    mv "$(dirname ${orig_cfg_file})/${ORIG_CONFIG_FILE_NAME}" ${orig_cfg_file} > /dev/null 2>&1
    rm "$(dirname ${orig_cfg_file})/${MOD_CONFIG_FILE_NAME}" > /dev/null 2>&1
}

function get_lxc_ctl_dom_proc_1_bind_mount_path {
    local cn_name=${1}

    echo "$(get_lxc_config_path)/${cn_name}/.ctl-dom-proc-1"
}

function get_lxc_config_option_in_cfg {
    local cfg_option=${1}
    local cfg_file=${2}

    if [ -e "${cfg_file}" ]; then
        cat ${cfg_file} | sed 's/[ \t]//g' | grep "^${cfg_option}" | cut -d '=' -f 2
    else
        echo ""
    fi
}

function get_lxc_config_seg_files {
    local cfg_file=${1}
    local child_cfg_segment_files=""
    local i=""

    # Here we recursive to find all the included segment cfg files
    local parent_cfg_segment_files=$(get_lxc_config_option_in_cfg "lxc.include" ${cfg_file})
    for i in ${parent_cfg_segment_files}; do
        child_cfg_segment_files="${child_cfg_segment_files} $(get_lxc_config_seg_files ${i})"
    done
    echo "${child_cfg_segment_files} ${cfg_file}"
}

function get_lxc_config_option_list {
    local cfg_option=${1}
    local cfg_file=${2}
    local cfg_segment_files=""
    local values_list=""
    local i=""

    cfg_segment_files=$(get_lxc_config_seg_files ${cfg_file})
    for i in ${cfg_segment_files}; do
        values_list="${values_list} $(get_lxc_config_option_in_cfg ${cfg_option} ${i})"
    done
    echo ${values_list}
}

function get_lxc_config_option {
    local cfg_option=${1}
    local cfg_file=${2}

    # Get list of values associated with this option then picks out the last value
    # in the list.
    echo "$(get_lxc_config_option_list ${cfg_option} ${cfg_file} | awk '{print $NF}')"
}

function are_pids_same_namespace {
    local namespace=${1}
    local pid1=${2}
    local pid2=${3}

    pid1_ns_id=$(ls -l /proc/${pid1}/ns | grep "${namespace} -> ${namespace}:" | sed -e 's/.*\[//' -e 's/\]//')
    pid2_ns_id=$(ls -l /proc/${pid2}/ns | grep "${namespace} -> ${namespace}:" | sed -e 's/.*\[//' -e 's/\]//')
    [ "${pid1_ns_id}" == "${pid2_ns_id}" ] && return 1
    return 0
}
