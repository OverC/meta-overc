#
# Use a "here" document, as it saves the SRC_URI and FILES_PN
# stuff for something otherwise so simple.
#

do_install_append() {
	cat <<EOF > ${S}/mdadm.conf.sample
# mdadm.conf
#
# Please refer to mdadm.conf(5) for information about this file.
#

# Remember!  If this file lives in your initrd, then changing it
# here is only 1/2 the job.  You will also need to update the initrd
# version; either with initrd-tools "update-initramfs" or manually via:
#
# mv /boot/initramfs-3.16.0-yocto-standard.img  /boot/initramfs-3.16.0-yocto-standard.img~
# cd /tmp
# rm -rf x ; mkdir x ; cd x
# zcat /boot/initramfs-3.16.0-yocto-standard.img~ | cpio -id -H newc
# cp -a /etc/mdadm/mdadm.conf ./etc/mdadm/mdadm.conf
# find . | cpio -o -H newc |gzip -9 > /boot/initramfs-3.16.0-yocto-standard.img

# by default, scan all partitions (/proc/partitions) for MD superblocks.
# alternatively, specify devices to scan, using wildcards if desired.
DEVICE partitions

# auto-create devices with Debian standard permissions
CREATE owner=root group=disk mode=0660 auto=yes

# automatically tag new arrays as belonging to the local system
HOMEHOST <system>

# instruct the monitoring daemon where to send mail alerts
MAILADDR root

# sample definitions of existing MD arrays
# ARRAY /dev/md0 metadata=1.2 devices=/dev/sda1,/dev/sdb1
# ARRAY /dev/md0 level=raid0 num-devices=2 UUID=a533f2d7:056ebe90:eb55ac39:69133cc1
EOF
	install -d -m 755 ${D}${sysconfdir}/mdadm
	install -m 644 ${S}/mdadm.conf.sample ${D}${sysconfdir}/mdadm/mdadm.conf
}
