do_install:append() {
	# if we take drm from the kernel, and a different set from libdrm
	# at different paths in the sysroot, then mayhem can result.
	rm -rf ${D}${exec_prefix}/include/drm
}
