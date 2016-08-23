#!/bin/sh

linkdevs="wl* en* eth*"

#Use one physical mac address as the system id
for dev in `(cd /sys/class/net && ls -d $linkdevs 2>/dev/null)`; do
    cat  /sys/class/net/$dev/address >/etc/system-id
    break
done

