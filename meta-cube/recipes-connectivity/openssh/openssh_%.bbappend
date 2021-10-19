do_install:append () {

    # make sure root can ssh in
    sed -i 's/#PermitRootLogin.*/PermitRootLogin yes/' ${D}${sysconfdir}/ssh/sshd_config
}
