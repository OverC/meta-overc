# Add a default network manager configuration file or the nm-applet
# will not run properly.  The NetworkManager should ignore br-int if
# it exists

do_install_append() {
	cat<<EOF>${D}/etc/NetworkManager/NetworkManager.conf
[main]
plugins=keyfile

[keyfile]
unmanaged-devices=interface-name:veth0;interface-name:veth-br-int
EOF

}
