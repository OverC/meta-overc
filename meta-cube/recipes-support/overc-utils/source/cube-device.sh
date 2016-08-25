#!/bin/sh
#
# Called from udev
#
# Attempt to map any added block devices to corresponding containers.


. /etc/cube-device/cube-device-functions

lxc_block_device_mgmt() {

	case "$DEVNAME" in
	mmcblk*|nvme*|nbd*|loop*)
        	dev=`echo "$DEVNAME" |sed 's/[a-z][0-9]//2'`
        	;;
	*)
        	dev=`echo "$DEVNAME" |sed 's/[0-9]//g'`
        	;;
	esac
	
	for line in `grep ^$dev $DEVICECONF 2>/dev/null`
	do
		container=`echo $line|cut -d@ -f2`
		log "$dev@$container"

		# check if targeted container is running
		is_container_running $container
		if [ $? -eq 0 ]; then
			log "target container $container is running"

			if [ "$ACTION" = "add" ]; then
				lxc_block_device_add_partition $container $DEVNAME
			elif [ "$ACTION" = "remove" ]; then
				lxc_block_device_remove_partition $container $DEVNAME
			fi
		else
			log "target container $container is NOT running"
		fi
	done

}

# we support lxc only for now
lxc_block_device_mgmt

