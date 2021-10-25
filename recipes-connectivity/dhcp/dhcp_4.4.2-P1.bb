require dhcp.inc

SRC_URI += "file://0001-define-macro-_PATH_DHCPD_CONF-and-_PATH_DHCLIENT_CON.patch \
            file://0002-dhclient-dbus.patch \
            file://0003-link-with-lcrypto.patch \
            file://0004-Fix-out-of-tree-builds.patch \
            file://0005-dhcp-client-fix-invoke-dhclient-script-failed-on-Rea.patch \
            file://0007-Add-configure-argument-to-make-the-libxml2-dependenc.patch \
            file://0009-remove-dhclient-script-bash-dependency.patch \
            file://0012-dhcp-correct-the-intention-for-xml2-lib-search.patch \
            file://0013-fixup_use_libbind.patch \
            file://0001-workaround-busybox-limitation-in-linux-dhclient-script.patch \
"

SRC_URI[md5sum] = "3089a1ebd20a802ec0870ae337d43907"
SRC_URI[sha256sum] = "b05e04337539545a8faa0d6ac518defc61a07e5aec66a857f455e7f218c85a1a"

LDFLAGS:append = " -pthread"

PACKAGECONFIG ?= ""
PACKAGECONFIG[bind-httpstats] = "--with-libxml2,--without-libxml2,libxml2"

CFLAGS += "-fcommon"
