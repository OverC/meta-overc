FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://vim-Make-text-mode-editing-Great-again.patch"

# Temporary workaround for absence upstream ; delete ASAP.
SRC_URI[md5sum] = "2bfd304eabd99fc57629851cd96bdbfd"
SRC_URI[sha256sum] = "54ec57275efec560452e0979269c88143d2ccae28e6db4b80e4f8940582274e6"
