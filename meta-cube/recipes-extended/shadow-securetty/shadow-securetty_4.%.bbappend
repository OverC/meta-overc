do_install_append () {
    securetty_file=${D}${sysconfdir}/securetty

    echo '' >> $securetty_file
    echo '# Allow inter-container logins via pts/*' >> $securetty_file

    for i in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20; do
	echo "pts/$i" >> $securetty_file
    done
}
