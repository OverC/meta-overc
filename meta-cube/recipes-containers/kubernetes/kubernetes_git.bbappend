do_install_append() {
    replace_line="ExecStart=/usr/bin/kubelet\nEnvironment=KUBELET_EXTRA_ARGS=\"--fail-swap-on=false --resolv-conf=/etc/resolv.conf --cgroup-root=/ --cgroups-per-qos=false --enforce-node-allocatable=''\""
    sed -i  "s#ExecStart=/usr/bin/kubelet#$replace_line#" ${D}${systemd_unitdir}/system/kubelet.service

    install -d ${D}/${localstatedir}/lib/kubelet
    #echo "KUBELET_EXTRA_ARGS=--fail-swap-on=false --resolv-conf=/etc/resolv.conf  --cgroup-root=/  --cgroups-per-qos=false --enforce-node-allocatable=\"\""
}
