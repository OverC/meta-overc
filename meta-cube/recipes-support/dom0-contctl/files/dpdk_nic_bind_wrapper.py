#! /usr/bin/python

# To bind a dpdk drive to pci device, we either can work directly
# with /sys with the following sequences:
#
# echo "${vendor} ${dev_id}" >> /sys/bus/pci/drivers/${driver}/new_id
# echo "${pci}" >> /sys/bus/pci/drivers/${driver}/bind
#
# However as soon as writing into "new_id" all the pci devices which have
# the same "vendor" and "dev_id" will be probed as well there we need to
# unbind unintended bind pci devices.  The tool from dpdk
# /opt/dpdk/tools/dpdk_nic_bind.py does just that.  However dpdk_nic_bind.py
# has a list of hardcoded dpdk drivers which can be used. We need to be
# able to override this hardcode driver list.
#
# This dpdk_nic_bind_wrapper.py is wrapper of dpdk_nic_bind.py. It parses
# the first argument as the allowed dpdk driver and modify the hardcoded
# list before invoking dpdk_nic_bind.py.

import sys

sys.path.append('/opt/dpdk/tools')
import dpdk_nic_bind

if __name__ == "__main__":
    dpdk_nic_bind.dpdk_drivers = [sys.argv[1]]
    sys.argv.pop(1)
    dpdk_nic_bind.main()
