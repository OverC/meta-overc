
do_install_append() {
	# Rename man pages to prefix openssl10-*
	for f in `find ${D}${mandir} -type f`; do
		mv $f $(dirname $f)/openssl10-$(basename $f)
	done
	for f in `find ${D}${mandir} -type l`; do
		ln_f=`readlink $f`
		rm -f $f
		ln -s openssl10-$ln_f $(dirname $f)/openssl10-$(basename $f)
	done
}
