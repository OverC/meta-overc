SUMMARY ?= "Sample systemd system container"
DESCRIPTION ?= "A small systemd system container which will run \
                the application defined in IMAGE_INSTALL."
HOMEPAGE ?= "http://www.windriver.com"

LICENSE ?= "MIT"
LIC_FILES_CHKSUM ?= "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES:remove = "live"

TARGETNAME ?= "c3-systemd-container"

IMAGE_INSTALL:append = "systemd"

IMAGE_FEATURES = ""

NO_RECOMMENDATIONS = "1"

SERVICES_TO_DISABLE ?= " \
    systemd-udevd.service \
    systemd-udevd-control.socket \
    systemd-udevd-kernel.socket \
    proc-sys-fs-binfmt_misc.automount \
    sys-fs-fuse-connections.mount \
    sys-kernel-debug.mount \
    systemd-hwdb-update.service \
    serial-getty@ttyS0.service \
    dev-ttyS0.device \
"

SERVICES_TO_ENABLE ?= ""

disable_systemd_services () {
	SERVICES_TO_DISABLE="${SERVICES_TO_DISABLE}"
	if [ -n "$SERVICES_TO_DISABLE" ]; then
		echo "Disabling systemd services:"
		for service in $SERVICES_TO_DISABLE; do
			echo "    $service"
			systemctl --root="${IMAGE_ROOTFS}" mask $service > /dev/null >1
		done
	fi
}

enable_systemd_services () {
	SERVICES_TO_ENABLE="${SERVICES_TO_ENABLE}"
	if [ -n "$SERVICES_TO_ENABLE" ]; then
		echo "Enabling additional systemd services:"
		for service in $SERVICES_TO_ENABLE; do
			echo "    $service"
			systemctl --root="${IMAGE_ROOTFS}" enable $service > /dev/null >1
		done
	fi
}


ROOTFS_POSTPROCESS_COMMAND += "disable_systemd_services; enable_systemd_services;"

inherit image
