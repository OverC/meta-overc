#! /bin/bash

source $(dirname ${0})/lxc_common_helpers.sh
source $(dirname ${0})/lxc_driver_net.sh
source $(dirname ${0})/lxc_launch_group.sh

LAUNCH_CMD_DB_KEY="wr.launch_cmd"

function lxc_launch_if_fail_then_clean_up {
	local cfg_file=${1}
	local cn_name=${2}

	# Check to see if cn is activated
	is_cn_running ${cn_name}
	if [ $? -eq 0 ]; then
		# If cn is not started then do some cleanup
		lxc_remove_net ${cfg_file} ${cn_name}
		lxc_log "Error, cannot launch container ${cn_name}."
		restore_all_config_files ${cfg_file}
	fi
}

function lxc_launch_prepare {
	local cn_name=${1}

	# Make sure container is not running.
	is_cn_exist ${cn_name}
	[ $? -eq 0 ] && lxc_log "Error, container ${cn_name} does not exist." && return 1
	is_cn_running ${cn_name}
	[ $? -eq 1 ] && lxc_log "Error, container ${cn_name} is already running." && return 1

	# Save the original config files for restoring later
	cfg_file=$(get_lxc_default_config_file ${cn_name})
	save_orig_config_file ${cfg_file}

	# Setup networking

	## temporarily removed until cube-essential networking is available
	save_mod_config_file ${cfg_file}
	return 0

	lxc_add_net_hook_info_cfg ${cfg_file}

	# It might be the case that the network options in cfg file
	# were modified during container is up.  This will mess up the
	# networking clean up process later.  So save it.
	lxc_setup_net_remote_end ${cfg_file} ${cn_name}
	if [ $? -ne 0 ]; then
		lxc_log "Error, cannot start ${cn_name} container."
		lxc_log "       Some network connections cannot be prepared."
		restore_all_config_files ${cfg_file}
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

	write_key_db "${cn_name}" "${LAUNCH_CMD_DB_KEY}" "launch_peer_container ${cn_name}"

	lxc_launch_prepare ${cn_name}
	[ $? -ne 0 ] && return 1

	if [ -d "${host_proc_path}/1" ]; then
		exec_lxc_cmd_cn host lxc-start -n "${cn_name}" -d
	else
		lxc_log "ERROR: host proc path ${host_proc_path} does not exist."
		return 1
	fi

	lxc_launch_if_fail_then_clean_up ${cfg_file} ${cn_name}
}

function launch_nested_container {
	local cn_name=${1}
	local parent_cn_name=${2}

	write_key_db "${cn_name}" "${LAUNCH_CMD_DB_KEY}" "launch_nested_container ${cn_name} ${parent_cn_name}"

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

function launch_container {
	local cn_name=${1}

	write_key_db "${cn_name}" "${LAUNCH_CMD_DB_KEY}" "launch_container ${cn_name}"

	cfg_file=$(get_lxc_default_config_file ${cn_name})
	parent_container_name=$(get_lxc_config_option "wr.parent" ${cfg_file})
	if [ -n "${parent_container_name}" ]; then
		launch_nested_container ${cn_name} ${parent_container_name}
	else
		launch_peer_container ${cn_name}
	fi
}

function relaunch_container {
	local cn_name=${1}

	launch_cmd=$(get_key_db ${cn_name} "${LAUNCH_CMD_DB_KEY}")
	[ -z "${launch_cmd}" ] && lxc_log "Error, relaunch, can not get saved launch cmd for container ${cn_name}" && return 1
	stop_container ${cn_name}
	[ $? -ne 0 ] && lxc_log "Error, relaunch, cannot stop container ${cn_name}" && return 1
	${launch_cmd}
	return 0
}

function enter_container_ns {
	local cn_name=${1}

	is_cn_running ${cn_name}
	[ $? -eq 0 ] && lxc_log "Error, container ${cn_name} is not active." && return 1

	lxc_init_pid=$(get_lxc_init_pid_from_cn_name ${cn_name})
	if [ -n "${lxc_init_pid}" ]; then
		# nsenter (setns) will fail if user namespace of ${lxc_init_pid} pid is the same
		# as this user namespace of process running this script.
		are_pids_same_namespace "user" "1" "${lxc_init_pid}"
		[ $? -ne 1 ] && nsenter_opts="--user"
		nsenter ${nsenter_opts} --mount --uts --ipc --net --pid --target ${lxc_init_pid}
		return 0
	else
		# the container is a peer, we need to go through essential for the
		# namespace. This only works on dom0.
		cube-console --ns ${cn_name}
		return 0
	fi
}

function stop_container {
	local cn_name=${1}
	local child_name=""
	local ret=0

	is_cn_stopped ${cn_name}
	[ $? -ne 0 ] && return 1

	# When stopping container, we will use the mod config file to
	# do clean up especially for network.
	mod_cfg_file=$(get_lxc_mod_config_file ${cn_name})
	if [ ! -f "${mod_cfg_file}" ]; then
		lxc_log "Fatal Error, saved file ${mod_cfg_file} does not exist?"
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
		exec_lxc_cmd_cn ${cn_name} lxc-stop -n ${cn_name} -k
	else
		lxc_log "Error, child ${child_name} container is running"
		ret=1
	fi

	# Now clean up all the "remote end"
	lxc_remove_net ${mod_cfg_file} ${cn_name}
	restore_all_config_files $(get_lxc_default_config_file ${cn_name})
	return ${ret}
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

function display_info_container {
	local cn_name=${1}

	is_cn_exist ${cn_name}
	[ $? -eq 0 ] && lxc_log "Container ${cn_name} does not exist." && return 1
	is_cn_stopped ${cn_name}
	if [ $? -eq 0 ]; then
		exec_lxc_cmd_cn ${cn_name} lxc-info -n ${cn_name}
		parent_cn_name=$(get_parent_cn_name_from_cn_name ${cn_name})
		lxc_init_pid=$(get_lxc_init_pid_from_cn_name ${cn_name})
		lxc_mgr_pid=$(get_lxc_mgr_pid_from_cn_name ${cn_name})
		# we only get a init or mgr pid for a nested container. Consider proxying
		# this to essential if we really need the info
		if [ -n "${lxc_mgr_pid}" ]; then
			echo -e "Lxc mgr pid:\t${lxc_mgr_pid} ($(cat /proc/${lxc_mgr_pid}/comm))"
			echo -e "Parent:\t\t${parent_cn_name}"
			echo -e "Uid map:$(cat /proc/${lxc_init_pid}/uid_map)"
			echo -e "Gid map:$(cat /proc/${lxc_init_pid}/gid_map)"
		fi
	else
		echo -e "Name:\t\t${cn_name}"
		echo -e "State:\t\tSTOPPED"
	fi
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

function monitor_container {
	local cn_name=${1}

	[ -n "${cn_name}" ] && opts="-n ${cn_name}"
	exec_lxc_cmd_cn "${HOST_CN_NAME}" "lxc-monitor ${opts}"
}
