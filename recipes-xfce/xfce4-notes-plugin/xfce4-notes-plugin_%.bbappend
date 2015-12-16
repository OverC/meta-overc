do_install_append(){
    #disable notes autostart
    if test -e ${D}${sysconfdir}/xdg/autostart/xfce4-notes-autostart.desktop ; then
        rm -rf ${D}${sysconfdir}/xdg/autostart/xfce4-notes-autostart.desktop
    fi
}

