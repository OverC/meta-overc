# hack around broken multilib by introducing a wrapper that
# will call the lib32-gcc binaries directly when requested.
# Avoid a SRC_URI and a standalone script file since that can
# break gcc shared source builds as described here:
#	 https://bugs.launchpad.net/linaro-oe/+bug/1068735

do_install_append() {
	rm -f ${D}${bindir}/gcc
	cat <<EOF > ${SW}/gcc-wrapper.sh
#!/bin/bash
# hack around broken multilib by introducing a wrapper that
# will call the lib32-gcc binaries directly when requested.

echo \$* | grep -q -- -m32

if [ \$? != 0 ]; then
	x86_64-poky-linux-gcc "\$@"
else
	i586-pokymllib32-linux-gcc "\$@"
fi
EOF
	install -m 755 ${SW}/gcc-wrapper.sh ${D}${bindir}/gcc
}

