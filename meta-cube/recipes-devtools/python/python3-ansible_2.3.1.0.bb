inherit setuptools3
require python-ansible.inc

RDEPENDS_${PN} += "python3-pyyaml python3-jinja2 python3-modules"

SRC_URI += " \
    file://python3-ensure-py-scripts-use-py3-for-shebang.patch \
"
