# Allow root to control the audio server

do_install:append() {
	echo "allow-autospawn-for-root = yes" >> ${D}/etc/pulse/client.conf
}
