#
# Normally provided by busybox but we don't want that.
# We go with the RHEL/centos method and use links.
# The Debian/Ubuntu distros use stand alone scripts, but
# they may not be 100% portable to yocto.
#

do_install:append:class-target() {
	ln ${D}${sbindir}/useradd ${D}${sbindir}/adduser
	ln ${D}${sbindir}/userdel ${D}${sbindir}/deluser

	ln ${D}${sbindir}/groupadd ${D}${sbindir}/addgroup
	ln ${D}${sbindir}/groupdel ${D}${sbindir}/delgroup

	if [ -d "${D}${mandir}/man8" ] ; then
		cd ${D}${mandir}/man8 && ln -s useradd.8 adduser.8
		cd ${D}${mandir}/man8 && ln -s userdel.8 deluser.8

		cd ${D}${mandir}/man8 && ln -s groupadd.8 addgroup.8
		cd ${D}${mandir}/man8 && ln -s groupdel.8 delgroup.8
	fi
}
