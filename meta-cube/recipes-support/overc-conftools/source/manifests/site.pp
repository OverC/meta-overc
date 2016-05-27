# Configuration data. See overc-conftools package for details.
# Note many of these are set by the installer.
$network_prime_container = "cube-server"
$network_prime_device = "enp0s3"

# network_prime_container will have an offset of 1
# -essential will always have an offset of 2
#$network_offsets = ['dom0,3','cube-server,4']
$network_offsets = []

# Setup the Network Prime container. Enabled during first
# boot uncomment to enable during the _next_ boot.
$configure_network_prime = true

if $configure_network_prime == true {
  class { '::network_prime':
    container => "$network_prime_container",
    network_device => "$network_prime_device",
    network_offsets => $network_offsets,
  }
  # Used to ensure networking 'restart' happens after configuration.
  $networking_require = [ Class["::network_prime"] ]
  exec { 'lxc-autonetdev':
    command => "/var/lib/lxc/$network_prime_container/autonetdev",
    require => $networking_require,
  }
}

# The overc-conftools service runs after the network.target
# but may contain networking modifications, so restart the
# systemd-networkd to ensure these changes take effect.
exec { 'restart-networking':
  command => '/bin/systemctl restart systemd-networkd',
  before => Exec['restart-dnsmasq'],
  require => $networking_require,
}
exec { 'restart-dnsmasq':
  command => '/bin/systemctl restart dnsmasq',
}
