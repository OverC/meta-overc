# Extend PACKAGECONFIG to include good stuff beyond qemu.inc
PACKAGECONFIG:append:x86-64:class-target += "linux-aio virtfs libcap-ng vhost"
PACKAGECONFIG:append = " linux-aio virtfs libcap-ng vhost"

QEMU_TARGETS:class-target = "arm aarch64 i386 x86_64"

PACKAGES:prepend:class-target = "${PN}-x86_64 \
                     ${PN}-aarch64 \
                     ${PN}-arm \
                     ${PN}-i386 \
                    "

FILES:${PN}-x86_64:class-target = "${bindir}/qemu-system-x86_64 ${bindir}/qemu-x86_64"
RDEPENDS:${PN}-x86_64:append_class_target = "${PN}"
INSANE_SKIP:${PN}-x86_64:class-target = "file-rdeps"

FILES:${PN}-i386:class-target = "${bindir}/qemu-system-i386 ${bindir}/qemu-i386"
RDEPENDS:${PN}-i386:append:class-target = "${PN}"
INSANE_SKIP:${PN}-i386:class-target = "file-rdeps"

FILES:${PN}-aarch64:class-target = "${bindir}/qemu-system-aarch64 ${bindir}/qemu-aarch64"
RDEPENDS:${PN}-aarch64:append:class-target = "${PN}"
INSANE_SKIP:${PN}-aarch64:class-target = "file-rdeps"

FILES:${PN}-arm:class-target = "${bindir}/qemu-system-arm ${bindir}/qemu-arm"
RDEPENDS:${PN}-arm:append:class-target = "${PN}"
INSANE_SKIP:${PN}-arm:class-target = "file-rdeps"
