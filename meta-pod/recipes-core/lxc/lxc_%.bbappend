# cgmanager is needed for running unpriv containers

PACKAGECONFIG_append = " cgmanager"

PACKAGECONFIG[cgmanager] = "--enable-cgmanager=yes,--enable-cgmanager=no,cgmanager,cgmanager"

SYSTEMD_AUTO_ENABLE_${PN}-setup = "enable"
