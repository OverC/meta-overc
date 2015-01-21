PACKAGECONFIG ?= "bash-completion"

# no config args for with, or w/o hence _two_ commas.
PACKAGECONFIG[bash-completion] = ",,bash-completion,bash-completion ${BPN}-bash-completion"

do_install_append() {
	install -d ${D}/${datadir}/bash-completion/completions/
	install -m 644 ${S}/contrib/completion/git-completion.bash ${D}/${datadir}/bash-completion/completions/git
}

PACKAGES =+ "${BPN}-bash-completion"
FILES_${BPN}-bash-completion = "${datadir}/bash-completion/completions/*"
