do_install_append() {
    # The mouse being autoenabled is just annoying; delete the block.
    sed -i '/the mouse works just fine/,~4d' ${D}/${datadir}/${PN}/vimrc
    # The default of keeping backup and undo files clutters "git status"
    # output, so use the VMS settings of nobackup for unix
    sed -i 's/has("vms")/has("unix")/' ${D}/${datadir}/${PN}/vimrc
    # The default of incremental searching causes the screen to jump
    # all over the place and makes you forget to hit enter to finalize
    # the search, so disable it
    sed -i 's/^set incsearch/# set incsearch/' ${D}/${datadir}/${PN}/vimrc
    # Jumping to the last known position in a file is annoying when a
    # temporary git commit log buffer always has the same file name, but
    # never the same content.
    sed -i '/always jump to the last known cursor position/,+9d' ${D}/${datadir}/${PN}/vimrc
}
