#!/bin/bash

OVERC=/opt/overc-system-agent/overc
OVERC_SERVER=/opt/overc-system-agent/run_server.py
SVR_PORT=12345

OVERC_DOM0=/etc/overc/container/dom0

# test url for send_image
url=http://openlinux.windriver.com/overc/images/genericx86-64/cube-desktop-genericx86-64.tar.bz2

count_snapshots() {
	local container=$1
	local found_snapshot=0
	local snapshot_cnt=0
	while read i; do
		if [ $found_snapshot -eq 0 ]; then
			if `echo $i | grep "List snapshots on container:" 1>/dev/null 2>&1`; then
				found_snapshot=1
			fi
		else
			if `echo $i | grep " -> " 1>/dev/null 2>&1`; then
				snapshot_cnt=$(expr $snapshot_cnt + 1)
			fi
		fi
	done < <($OVERC container list_snapshots $container dom0)
	return $snapshot_cnt
}

test_container_status() {
	local container=$1
	local expected_status=$2

	$OVERC_DOM0 -A -n $container
	status=$?
	if [ $status -ne $expected_status ]; then
		echo "Error! $container container should be active"
		echo "Expected value $expected_status, got $status"
		exit 1
	fi
}

# start up REST server
$OVERC_SERVER -p $SVR_PORT &
svr_pid=$!
echo "REST server started at pid: $svr_pid"

#################################################
# test status
# on bootup, dom0, dom1 and cube-desktop containers should be active
#################################################
test_container_status dom0 3
test_container_status dom1 3
test_container_status cube-desktop 3
test_container_status invalid_container 0

#################################################
# test start/stop
#################################################
echo "Test: Trying to start a container that's already active"
$OVERC container start cube-desktop dom0
status=$?
if [ $status -ne 1 ]; then
	echo "Error! Starting an active container should fail"
	echo "Expected value 1, got $status"
	exit 1
else
	echo "OK: failed"
fi
echo "Test: Trying to stop a container that's active"
$OVERC container stop cube-desktop dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Stopping an active container should succeed"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi
echo "Test: Trying to start a container that's inactive"
$OVERC container start cube-desktop dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Starting an inactive container should succeed"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi

#################################################
# update and sync testing
#################################################
echo "Test: Update image"
$OVERC container update dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to perform update on system images"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi

echo "Test: send_image image on url $url"
$OVERC container send_image $url dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to perform send_image on system images"
	echo "using url: $url"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi

#################################################
# test activate rollback on cube-desktop
#################################################
echo "Test: Setting up file marker before activate/rollback test"
touch /essential/var/lib/lxc/cube-desktop/rootfs/root/test

count_snapshots cube-desktop
orig_num_snapshot=$?

echo "Test: activate container"
$OVERC container activate cube-desktop dom0 -f
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to activate container"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi

echo "Test: Looking for file marker after activate"
if [ -e /essential/var/lib/lxc/cube-desktop/rootfs/root/test ]; then
	echo "Error! File /essential/var/lib/lxc/cube-desktop/rootfs/root/test should not exist!"
	exit 1
else
	echo "OK: file marker doesn't exist"
fi

echo "Test: counting snapshots after activate"
expected_num_snapshot=$(expr $orig_num_snapshot + 1)
count_snapshots cube-desktop
num_snapshot=$?
if [ $num_snapshot -ne $expected_num_snapshot ]; then
	echo "Error! Number of snapshots should be $expected_num_snapshot [got $num_snapshot]"
	exit 1
else
	echo "OK: $num_snapshot snapshot"
fi

echo "Test: container should be active after active, stopping container"
$OVERC container stop cube-desktop dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Stopping an active container should succeed"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi

echo "Test: rollback container"
$OVERC container rollback cube-desktop  dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to perform container rollback"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi

echo "Test: counting snapshots after rollback"
count_snapshots cube-desktop
num_snapshot=$?
if [ $num_snapshot -ne $orig_num_snapshot ]; then
	echo "Error! Number of snapshots should be $orig_num_snapshot [got $num_snapshot]"
	exit 1
else
	echo "OK: $num_snapshot snapshot"
fi

$OVERC container start cube-desktop dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Starting an inactive container should succeed"
	echo "Expected value 0, got $status"
	exit 1
fi

echo "Test: Looking for file marker after rollback"
if [ ! -e /essential/var/lib/lxc/cube-desktop/rootfs/root/test ]; then
	echo "Error! File /essential/var/lib/lxc/cube-desktop/rootfs/root/test should exist!"
	exit 1
else
	echo "OK: file marker found"
fi
rm /essential/var/lib/lxc/cube-desktop/rootfs/root/test
#
#################################################
# test operations on dom0
#################################################
echo "Test: Attempting to stop on container dom0"
$OVERC container stop dom0 dom0
status=$?
if [ $status -ne 1 ]; then
	echo "Error! Stopping dom0 container should fail"
	echo "Expected value 1, got $status"
	exit 1
else
	echo "OK: failed as expected"
fi

count_snapshots dom0
orig_num_snapshot=$?

echo "Test: Attempting to start on container dom0"
$OVERC container start dom0 dom0
status=$?
if [ $status -ne 1 ]; then
	echo "Error! Starting dom0 container should fail"
	echo "Expected value 1, got $status"
	exit 1
else
	echo "OK: failed as expected"
fi

echo "Test: Setting up file marker before activate/rollback test"
touch /root/test

echo "Test: activate container dom0"
$OVERC container activate dom0 dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to activate container"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi

echo "Test: Looking for file marker after activate"
if [ ! -e /root/test ]; then
	echo "Error! /root/test should exist"
	exit 1
else
	echo "OK: file marker persists on current running system"
fi
if [ -e /essential/var/lib/lxc/dom0/rootfs/root/test ]; then
	echo "Error! /essential/var/lib/lxc/dom0/rootfs/root/test should not exist"
	exit 1
else
	echo "OK: file marker doesn't exist from /essential/var/lib/lxc/dom0/rootfs"
fi

echo "Test: counting snapshots after activate container dom0"
expected_num_snapshot=$(expr $orig_num_snapshot + 1)
count_snapshots dom0
num_snapshot=$?
if [ $num_snapshot -ne $expected_num_snapshot ]; then
	echo "Error! Number of snapshots should be $expected_num_snapshot [got $num_snapshot]"
	exit 1
else
	echo "OK: $num_snapshot snapshot"
fi

echo "Test: rollback container dom0"
$OVERC container rollback dom0  dom0
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to perform container rollback"
	echo "Expected value 0, got $status"
	exit 1
else
	echo "OK: succeeded"
fi

echo "Test: Looking for file marker after rollback"
if [ ! -e /root/test ]; then
	echo "Error! /root/test should exist"
	exit 1
else
	echo "OK: file marker persists on current running system"
fi
if [ ! -e /essential/var/lib/lxc/dom0/rootfs/root/test ]; then
	echo "Error! /essential/var/lib/lxc/dom0/rootfs/root/test should exist"
	exit 1
else
	echo "OK: file marker exists from /essential/var/lib/lxc/dom0/rootfs"
fi
rm /root/test

echo "Test: counting snapshots after rollback container dom0"
count_snapshots dom0
num_snapshot=$?
if [ $num_snapshot -ne $orig_num_snapshot ]; then
	echo "Error! Number of snapshots should be $orig_num_snapshot [got $num_snapshot]"
	exit 1
else
	echo "OK: $num_snapshot snapshot"
fi

#################################################
# test from REST
#################################################
echo "Test: Talking to REST server"
R=`printf "GET /container/list?template=dom0\r\n\r\n" | nc localhost $SVR_PORT`
if `echo $R|grep "List container" 1>/dev/null 2>&1`; then
	echo "REST server: got container list"
else
	echo "Error! Failed to list containers from REST server"
	kill -9 $svr_pid
	exit 1
fi
kill -9 $svr_pid

#################################################
# test packages updates
#################################################
echo "Test: updating package database"
$OVERC host update
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to update package database"
	exit 1
else
	echo "OK: succeeded"
fi

echo "Test: upgrade newer"
$OVERC host newer
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to check for new packages"
	exit 1
else
	echo "OK: succeeded"
fi

echo "Test: upgrade packages"
$OVERC host upgrade
status=$?
if [ $status -ne 0 ]; then
	echo "Error! Failed to upgrade packages"
	exit 1
else
	echo "OK: succeeded"
fi
exit 0
