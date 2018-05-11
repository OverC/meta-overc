#!/bin/sh

linkdevs="wl* en* eth*"
containers="dom0 cube-gw cube-server cube-desktop"

#Use one physical mac address as the system id
for dev in `(cd /sys/class/net && ls -d $linkdevs 2>/dev/null)`; do
    cat  /sys/class/net/$dev/address >/etc/system-id
    for cn in $containers; do
        if [ -d /opt/container/$cn/rootfs/etc ]; then
            cat /sys/class/net/$dev/address >/opt/container/$cn/rootfs/etc/system-id
        fi
    done
    break
done

