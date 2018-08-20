#!/bin/sh

SYSROOT_TARBALL=""
SYSROOT=""

function get_ref(){
    count=0
    logfile=$(mktemp /tmp/tmp.XXXXXXXXXX)
    ostree admin status | sed 's/\*/\\\\\*/' >$logfile
     while read fileline; do
        echo $fileline | grep '\*' >/dev/null 2>&1
	if [ $? == 0 ]; then
		SYSROOT=$(echo $fileline | awk '{print "/ostree/deploy/"$2"/deploy/"$3}')
            count=1
	fi

	if [ $count == 1 ]; then
            echo $fileline | grep " *origin refspec"  >/dev/null 2>&1
	    if [ $? == 0 ]; then
                SYSROOT_TARBALL=$(echo $fileline | awk '{print $3}' | sed 's/:/-/g')
		SYSROOT_TARBALL=${SYSROOT_TARBALL}.tar.gz
		rm -rf $logfile
		break
	    fi
	fi
    done<$logfile
}

get_ref

#deploy the /etc directory if the system is in unlocked status
mount | grep "^overlay" | grep "lowerdir=usr" | grep "upperdir=.usr-ovl-upper" >/dev/null 2>&1
if [ $? == 0 ]; then
    cp -a /etc /usr/
fi

tar --exclude="./usr" --exclude="./etc"  --exclude="./.usr-ovl-*" --xattrs --xattrs-include='*' -cf - /usr -C $SYSROOT  . -P | pv -s $(du -sb $SYSROOT | awk '{print $1}') | gzip > $SYSROOT_TARBALL

echo "The system had been exported to ./$SYSROOT_TARBALL"
