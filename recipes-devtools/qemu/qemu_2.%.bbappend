# Extend PACKAGECONFIG to include good stuff beyond qemu.inc
PACKAGECONFIG_append_x86-64_class-target += "linux-aio virtfs vhost"
