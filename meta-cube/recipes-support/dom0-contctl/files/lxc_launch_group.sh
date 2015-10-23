#! /bin/bash

source $(dirname ${0})/lxc_common_helpers.sh

function launch_group {
    local groups_to_start=${1}
    local cn_list=$(lxc-ls)
    local current_cn_name=$(get_cn_name_from_init_pid 1)

    [ -z "${groups_to_start}" ] && lxc_log "Info, please provide what group to start." && return 1

    for cn in ${cn_list}; do
        # Do not launching the current container that is running
        # this script
        if [ "${current_cn_name}" == "${cn}" ]; then
            continue
        fi

        cfg_file=$(get_lxc_default_config_file ${cn})
        cn_autostart=$(get_lxc_config_option "wr.start.auto" ${cfg_file})
        cn_group=$(get_lxc_config_option "lxc.group" ${cfg_file} | sed 's/,/ /g')
        cn_order=$(get_lxc_config_option "lxc.start.order" ${cfg_file})
        [ -z "${cn_order}" ] && cn_order="1"

        # If this container is part of autostart for a group then put it in
        # the list of container to autostart for that group
        for g in ${groups_to_start}; do
            if [ "${cn_autostart}" == "1" ]; then
                matched="no"
                for i in ${cn_group}; do
                    [ "${i}" == "${g}" ] && { matched="yes"; break; }
                done
                [ "${matched}" == "yes" ] && eval "cn_list_${g}=\"\${cn_list_${g}}\n${cn_order} ${cn}\""
             fi
        done
    done

    # Now we have the list of containers to autostart per group,
    # do a sort on the orders associated with each container
    for g in ${groups_to_start}; do
        eval "res=\${cn_list_${g}}"
        cn_list_to_launch=$(echo -e "$res" | sort -r -n)
        # ${cn_list_to_launch} at this point has this format:
        #     <container B order>
        #     <container B name>
        #     <container A order>
        #     <container A name>
        # So we want to pick out every second line of each container,
        # and this done is done through variable ${i}
        i=0
        for cn in ${cn_list_to_launch}; do
            if [ ${i} -eq 1 ]; then
                lxc_log "Info, auto-launching container ${cn}."
                launch_container ${cn}
                cn_delay=$(get_lxc_config_option "lxc.start.delay" ${cfg_file})
                [ -n "${cn_delay}" ] && sleep ${cn_delay}
                i=0
                continue
            fi
            i=$((i+1))
        done
    done
}
