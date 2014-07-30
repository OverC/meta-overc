do_install_append() {
    # The mouse being autoenabled is just annoying; delete the block.
    sed -i '/the mouse works just fine/,~4d' ${D}/${datadir}/${PN}/vimrc
}
