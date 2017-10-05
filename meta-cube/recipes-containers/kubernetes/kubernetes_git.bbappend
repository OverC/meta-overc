do_install_append() {
    replace_line="ExecStart=/usr/bin/kubelet\nEnvironment=KUBELET_EXTRA_ARGS=--fail-swap-on=false"
    sed -i  "s#ExecStart=/usr/bin/kubelet#$replace_line#" ${D}${systemd_unitdir}/system/kubelet.service
}
