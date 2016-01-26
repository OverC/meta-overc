# Configuration data. See overc-conftools package for details.
# Note many of these are set by the installer.
$network_prime_container = "domE"
$network_prime_device = "enp0s3"

# Setup the Network Prime container. Enabled during first
# boot uncomment to enable during the _next_ boot.
$configure_network_prime = true

if $configure_network_prime == true {
  class { '::network_prime':
    container => "$network_prime_container",
    network_device => "$network_prime_device",
  }
}

# The overc-conftools service runs after the network.target
# but may contain networking modifications, so restart the
# systemd-networkd to ensure these changes take effect.
exec { 'restart-networking':
  command => '/bin/systemctl restart systemd-networkd',
}

# Start dnsmasq to hand out IP addresses on the internal net
#exec { 'start-dnsmasq':
#  command => '/usb/bin/dnsmasq --interface=br-int --except-interface=lo --dhcp-range=192.168.0.100,192.168.0.200',
#}
