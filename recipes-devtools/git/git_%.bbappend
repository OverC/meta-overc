# Auto-include git-bash-completion if bash-completion is present.

PACKAGECONFIG ?= "bash-completion"

# no config args for with, or w/o hence _two_ commas.
PACKAGECONFIG[bash-completion] = ",,bash-completion,bash-completion ${BPN}-bash-completion"
