# Extend PACKAGECONFIG to include good stuff beyond qemu.inc
PACKAGECONFIG_append_x86-64_class-target += "linux-aio virtfs libcap-ng vhost"
PACKAGECONFIG_append = " linux-aio virtfs libcap-ng vhost"

QEMU_TARGETS_class-target = "arm aarch64 i386 x86_64"

PACKAGES_prepend_class-target = "${PN}-x86_64 \
                     ${PN}-aarch64 \
                     ${PN}-arm \
                     ${PN}-i386 \
                    "

FILES_${PN}-x86_64_class-target = "${bindir}/qemu-system-x86_64 ${bindir}/qemu-x86_64"
RDEPENDS_${PN}-x86_64_append_class_target = "${PN}"
INSANE_SKIP_${PN}-x86_64_class-target = "file-rdeps"

FILES_${PN}-i386_class-target = "${bindir}/qemu-system-i386 ${bindir}/qemu-i386"
RDEPENDS_${PN}-i386_append_class-target = "${PN}"
INSANE_SKIP_${PN}-i386_class-target = "file-rdeps"

FILES_${PN}-aarch64_class-target = "${bindir}/qemu-system-aarch64 ${bindir}/qemu-aarch64"
RDEPENDS_${PN}-aarch64_append_class-target = "${PN}"
INSANE_SKIP_${PN}-aarch64_class-target = "file-rdeps"

FILES_${PN}-arm_class-target = "${bindir}/qemu-system-arm ${bindir}/qemu-arm"
RDEPENDS_${PN}-arm_append_class-target = "${PN}"
INSANE_SKIP_${PN}-arm_class-target = "file-rdeps"
