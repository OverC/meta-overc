# Allow root to control the audio server

do_install_append() {
	echo "allow-autospawn-for-root = yes" >> ${D}/etc/pulse/client.conf
}
