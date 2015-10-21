#
# Copyright (C) 2015 Wind River Systems, Inc.
#

FILESPATH_append := ":${@base_set_filespath(['${THISDIR}'], d)}/${PN}"
SRC_URI += "file://smartpm-add-a-builddep-command-to-install-all-of-the.patch \
           "
