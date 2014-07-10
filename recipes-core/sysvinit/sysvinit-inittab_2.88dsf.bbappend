SYSVINIT_ENABLED_CTRLALTDEL ?= "0"

do_install_append () {
	if [ "${SYSVINIT_ENABLED_CTRLALTDEL}" = "1" ]; then
        	cat <<EOF >>${D}${sysconfdir}/inittab
ca:12345:ctrlaltdel:/sbin/shutdown -t1 -a -r now

EOF
	fi
} 
