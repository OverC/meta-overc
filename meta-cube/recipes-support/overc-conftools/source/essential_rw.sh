rw_test=`mount |grep " / "|grep rw`

if [ -z "$rw_test" ]; then
	if read -t 5 -p "Essential is read-only, if you need login as read-write, please enter \"yes\":" rw_allow
	then
		if [ "$rw_allow" == "yes" ]; then
			mount / -o remount,rw
		fi
	fi
else
	if read -t 5 -p "Essential is read-write, if you need login as read-only, please enter \"yes\":" rw_allow
	then
		if [ "$rw_allow" == "yes" ]; then
			umount /
		fi
	fi
fi
echo
rw_test=`mount |grep " / "|grep rw`

if [ ! -z "$rw_test" ]; then
	echo "Warning, Essential rootfs is in read-write, all modification on essential will be recorded."
	echo "To return read-only, please issue \"umount /\" or reboot system."
else
	echo "Essential is in read-only."
fi
