# Temporarily work around double starting of X desktop services The
# respective session managers for xfce and gnome properly read the
# autostart directory and this script added by meta-openembedded
# causes these services to run a second time.

do_install_append() {
	rm -f ${D}/etc/X11/Xsession.d/*xdgautostart.sh
}
