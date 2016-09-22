class network_prime
(
  $container = $network_prime::container,
  $network_device = $network_prime::network_device,
  $network_offsets = $network_prime::network_offsets,
) {

  # Static IP address for cube-essential connected to br-int
  host { 'cube-essential':
    ip => '192.168.42.2',
  }

  # Let networkd configure br-int - network-prime container
  file { '25-br-int.network':
    path => "/var/lib/lxc/$container/rootfs/etc/systemd/network/25-br-int.network",
    source => 'puppet:///modules/network_prime/25-br-int.network',
  }

  # Let networkd configure veth0 (br-int virtual interface) - network-prime container
  file { '20-br-int-virt.network':
    path => "/var/lib/lxc/$container/rootfs/etc/systemd/network/20-br-int-virt.network",
    source => 'puppet:///modules/network_prime/20-br-int-virt.network',
  }

  # Remove the default networking configuration - network-prime container
  file { '20-wired.network':
    path => "/var/lib/lxc/$container/rootfs/etc/systemd/network/20-wired.network",
    ensure => 'absent',
  }

  # Create the br-int OVS bridge
  vs_bridge { 'br-int':
    ensure => present,
    before => File['25-br-int.network.essential'],
  }

  # Let networkd configure br-int - essential
  file { '25-br-int.network.essential':
    path => "/etc/systemd/network/25-br-int.network",
    source => 'puppet:///modules/network_prime/25-br-int.network.essential',
  }

  # Remove the default networking configuration - network-prime container
  file { '20-wired.network.essential':
    path => "/etc/systemd/network/20-wired.network",
    ensure => 'absent',
  }

  # If network_device is empty, it means doesn't use networkd
  file { '30-wired.network':
    path => "/var/lib/lxc/$container/rootfs/etc/systemd/network/30-wired.network",
    content => "[Match]\nName=$network_device\n\n[Network]\nDHCP=ipv4\nIPForward=ipv4\n",
    before => Exec['check_networkdevice'],
  }

  exec { 'check_networkdevice':
    command => "/bin/rm -f /var/lib/lxc/$container/rootfs/etc/systemd/network/30-wired.network;/bin/ln -sf /dev/null /var/lib/lxc/$container/rootfs/etc/systemd/system/systemd-resolved.service",
    onlyif => "/usr/bin/test -z $network_device",
  }

  exec { 'disable_named_service':
    command => "/bin/ln -sf /dev/null /var/lib/lxc/$container/rootfs/etc/systemd/system/named.service",
  }

  # Service files and script to make sure the network-prime is properly
  # configured (OVS, iptables...) on boot.
  file { 'overc-network-prime.service':
    path => "/var/lib/lxc/$container/rootfs/etc/systemd/system/overc-network-prime.service",
    source => 'puppet:///modules/network_prime/overc-network-prime.service',
    before => File['overc-network-prime.service.link'],
  }
  file { 'overc-network-prime-port-forward.service':
    path => "/var/lib/lxc/$container/rootfs/etc/systemd/system/overc-network-prime-port-forward.service",
    source => 'puppet:///modules/network_prime/overc-network-prime-port-forward.service',
    before => File['overc-network-prime-port-forward.service.link'],
  }
  file { 'autonetdev':
    path => "/var/lib/lxc/$container/autonetdev",
    source => 'puppet:///modules/network_prime/autonetdev',
    before => File['overc-network-prime.service.link'],
  }
  file { 'overc-network-prime-port-forward.service.link':
    ensure => 'link',
    target => "../overc-network-prime-port-forward.service",
    path => "/var/lib/lxc/$container/rootfs/etc/systemd/system/multi-user.target.wants/overc-network-prime-port-forward.service",
  }
  file { 'overc-network-prime.service.link':
    ensure => 'link',
    target => "../overc-network-prime.service",
    path => "/var/lib/lxc/$container/rootfs/etc/systemd/system/multi-user.target.wants/overc-network-prime.service",
  }
  file { '/etc/overc':
     path => "/var/lib/lxc/$container/rootfs/etc/overc",
     ensure => 'directory',
     before => File['network_prime.sh'],
  }
  exec { 'remove_resolv.conf_produce_by_systemd_resolved':
    command => "/bin/rm -rf /etc/resolv.conf", 
    before => File['/etc/resolv.conf'],
  }
  file { '/etc/resolv.conf':
    path => "/etc/resolv.conf",
    content => "nameserver 192.168.42.1\n",
    mode => '0666',
  }
  # Setup dnsmasq on -networkprime to bind to 192.168.42.1
  file_line { 'dnsmasq-netprime':
    path => "/var/lib/lxc/$container/rootfs/etc/dnsmasq.conf",
    match => '^listen-address=*',
    line => 'listen-address=192.168.42.1',
  }
  # Ensure dnsmasq starts the first time by touching the /etc/resolv.conf
  file { 'resolve-conf-netprime':
    path => "/var/lib/lxc/$container/rootfs/etc/resolv.conf",
    ensure => present,
    mode => '0666',
  }
  # turn off dnsmasq on dom0 if netprime is not dom0
  exec { 'dnsmasq-dom0':
    command => "/bin/rm -f /var/lib/lxc/dom0/rootfs/etc/systemd/system/multi-user.target.wants/dnsmasq.service",
    onlyif => "/usr/bin/test $container != dom0",
  }

  file { 'network_prime.sh':
    path => "/var/lib/lxc/$container/rootfs/etc/overc/network_prime.sh",
    content => template('network_prime/network_prime.sh.erb'),
    mode => '0750',
  }
  file { 'network_prime_port_forward.sh':
    path => "/var/lib/lxc/$container/rootfs/etc/overc/network_prime_port_forward.sh",
    content => template('network_prime/network_prime_port_forward.sh.erb'),
    mode => '0750',
  }
  # The network-prime has to be able to forward external traffic
  file_line { 'enable-ip-forwarding-config':
    path => "/var/lib/lxc/$container/rootfs/etc/sysctl.conf",
    match => '.*net.ipv4.ip_forward=[01]',
    line => 'net.ipv4.ip_forward=1',
  }

  # Set container network offset and gateway. Containers not
  # found in $network_offset_map should be setup for DHCP.
  # This expects 20-wired.network to exists and have 'DHCP=' specified.
  define set_network_offset {
    $offset = split($name, ',')
    file_line { "${offset[0]}.offset":
      path => "/var/lib/lxc/${offset[0]}/rootfs/etc/systemd/network/20-wired.network",
      match => '^DHCP=.*',
      line => "Address=192.168.42.${offset[1]}/24",
    }
    file_line { "${offset[0]}.gateway":
      path => "/var/lib/lxc/${offset[0]}/rootfs/etc/systemd/network/20-wired.network",
      line => 'Gateway=192.168.42.1',
    }
    file { "/var/lib/lxc/${offset[0]}/rootfs/etc/resolv.conf":
      path => "/var/lib/lxc/${offset[0]}/rootfs/etc/resolv.conf",
      content => "nameserver 192.168.42.2",
      mode => '0666',
    }
  }
  set_network_offset { $network_offsets: }

  # Setup dnsmasq on -essential
  file_line { 'dnsmasq-interface':
    path => '/etc/dnsmasq.conf',
    line => 'interface=br-int',
    before => File_line['dnsmasq-range-config'],
  }
  file_line { 'dnsmasq-range-config':
    path => '/etc/dnsmasq.conf',
    match => '^dhcp-range=*',
    line => 'dhcp-range=192.168.42.100,192.168.42.200,2h',
    before => File_Line['dnsmasq-set-gateway'],
  }
  file_line { 'dnsmasq-set-gateway':
    path => '/etc/dnsmasq.conf',
    line => 'dhcp-option=option:router,192.168.42.1',
  }

  # Disable configuration of the network prime on subsequent boots
  file_line { 'disable-network-prime-setup':
    path => '/etc/puppet/manifests/site.pp',
    match => '^\$configure_network_prime = true$',
    line => '#$configure_network_prime = true'
  }
}
