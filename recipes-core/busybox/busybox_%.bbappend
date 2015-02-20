do_install() {
	echo The busybox package is incompatible with meta-builder.
	echo Please fix your packages to not depend on it.
	/bin/false
}
