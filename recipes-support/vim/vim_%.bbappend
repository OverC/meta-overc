do_install_append() {
    # The mouse being autoenabled is just annoying; delete the block.
    sed -i '/the mouse works just fine/,~4d' ${D}/${datadir}/${PN}/vimrc
    # The default of keeping backup and undo files clutters "git status"
    # output, so use the VMS settings of nobackup for unix
    sed -i 's/has("vms")/has("unix")/' ${D}/${datadir}/${PN}/vimrc
}
