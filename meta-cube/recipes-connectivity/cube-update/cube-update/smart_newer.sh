#!/bin/sh
#
# Copyright (C) 2016 Wind River Systems, Inc.
#

if [ $# -eq 1 ]; then
    ContainerName="$1"
fi

if [ X"$ContainerName" == X"essential" ]; then
    #Update the essential packages information #
    cube-cmd cmd ifconfig -a > /tmp/$$.network.log
    cube-cmd cmd route -n >> /tmp/$$.network.log
    cube-cmd cmd smart update > /tmp/$$.smart-update.log
    cube-cmd cmd smart newer > /tmp/$$.smart-newer.log
elif [ X"$ContainerName" == X"dom0" ]; then
    #Update the dom0 container packages information #
    ifconfig -a > /tmp/$$.network.log
    route -n >> /tmp/$$.network.log
    smart update > /tmp/$$.smart-update.log
    smart newer > /tmp/$$.smart-newer.log
else
    #Update the system container packages information #
    cube-cmd lxc-attach -n $ContainerName -- ifconfig -a > /tmp/$$.network.log
    cube-cmd lxc-attach -n $ContainerName -- route -n >> /tmp/$$.network.log
    cube-cmd lxc-attach -n $ContainerName -- smart update > /tmp/$$.smart-update.log
    cube-cmd lxc-attach -n $ContainerName -- smart newer > /tmp/$$.smart-newer.log
fi

NumPkgs=`grep -v "Package Name" /tmp/$$.smart-newer.log | grep "|" | awk -F"|" '{print $1,$2}' | wc -l`

if [ $NumPkgs -gt 0 ]; then
    RET=$NumPkgs
else
    if [ X"`grep 'No interesting upgrades available' /tmp/$$.smart-newer.log`" != X"" ]; then
        RET=0
    elif [ X"`grep 'User name:' /tmp/$$.smart-newer.log`" != X"" ]; then
        RET=-1
    elif [ X"`grep 'Error: Network unreachable.' /tmp/$$.smart-newer.log`" != X"" ]; then
        echo "CUBEUPDATE: $ContainerName update on `date` failure with the network connection" >> /etc/cube-update/network.log
        cat /tmp/$$.network.log >> /etc/cube-update/network.log
        RET=-2
    else
        RET=0
    fi
fi

echo "$RET"
