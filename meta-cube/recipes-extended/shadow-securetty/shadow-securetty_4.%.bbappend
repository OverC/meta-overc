do_install_append () {
    securetty_file=${D}${sysconfdir}/securetty

    echo '' >> $securetty_file
    echo '# Allow inter-container login' >> $securetty_file

    for i in `seq 1 20`; do 
	echo "pts/$i" >> $securetty_file
    done
}
