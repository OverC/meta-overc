#!/bin/sh
#
# Copyright (C) 2016 Wind River Systems, Inc.
#

if [ $# -eq 1 ]; then
    ContainerName="$1"
fi

case $ContainerName in
    dom0)
        network_log="$(ifconfig -a;route -n 2>&1)"
        update_log="$(dnf -q check-update)"
        ;;
    essential)
        network_log="$(cube-cmd ifconfig -a;cube-cmd route -ni 2>&1)"
        update_log="$(cube-cmd dnf -q check-update)"
        ;;
    cube*)
        network_log="$(cube-ctl $ContainerName:ifconfig -a;cube-ctl $ContainerName:route -n 2>&1)"
        update_log="$(cube-ctl $ContainerName:dnf -q check-update)"
        ;;
    esac

RET=$(echo $update_log | sed '/^$/d' | wc -l)

if echo "$update_log" | grep -q "Network unreachable"; then
    echo "CUBEUPDATE: $ContainerName update on `date` failure with the network connection" >> /etc/cube-update/network.log
    cat "$network_log" >> /etc/cube-update/network.log
    RET=-1
fi

echo "$RET"
