# hack alert: the meta-oe commit 77d17425b [netcf: upgrade 0.2.3 -> 0.2.8]
# breaks systemd in a multilib environment with the following error:
#
#  mv: cannot stat '... netcf/0.2.8+gitAUTOINC+9158278ad3-r0/image/usr/lib64/systemd/system/*': No such file or directory
#
# systemd libraries are always in 'lib' and do not following the lib/lib64
# split that multlibs trigger.
#
# As such, the appaned should reference /usr/lib directory for the time being
#
# But to fix this in a bbappend, we have to keep the main recipe's do_install_append
# happy .. so we create the lib64 directory with a temp file, we then remove it in 
# our same bbappend. This avoids packaging errors, and keeps the recipes bbappend
# from erroring.
#
# We can drop this when the upstream recipe is fixed

do_install_prepend() {
    install -d ${D}${libdir}/systemd/system/
    touch ${D}${libdir}/systemd/system/t
}

do_install_append() {
    if ${@base_contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
       install -d ${D}${systemd_unitdir}/system
       mv ${D}/usr/lib/systemd/system/* ${D}${systemd_unitdir}/system/
       rm -rf ${D}/usr/lib//systemd/
    fi

    rm -rf ${D}${libdir}/systemd/system/
}

