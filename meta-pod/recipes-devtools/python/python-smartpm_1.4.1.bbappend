#
# Copyright (C) 2015 Wind River Systems, Inc.
#

FILESPATH_append := ":${@base_set_filespath(['${THISDIR}'], d)}/${PN}"
SRC_URI += "file://smartpm-add-a-builddep-command-to-install-all-of-the.patch \
           "
# Add the option to change the data-dir to '/usr/lib/smart/data-dir'
PACKAGECONFIG[change-data-dir] = ",,,"
OVERRIDES .= "${@['', ':CHG-DATA-DIR']['change-data-dir' in d.getVar('PACKAGECONFIG', True).split()]}"
SRC_URI_append_CHG-DATA-DIR = "file://python-smartpm-change-the-smart-data-dir-to-usr-lib-.patch"
