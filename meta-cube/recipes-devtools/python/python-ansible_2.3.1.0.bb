DESCRIPTION = "Ansible is a simple IT automation platform that makes your applications and systems easier to deploy."
HOMEPAGE = "https://github.com/ansible/ansible/"
SECTION = "devel/python"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

SRCNAME = "ansible"

SRC_URI = "http://releases.ansible.com/ansible/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "b1be8f05864a07c06b8a767dcd48ba1b"
SRC_URI[sha256sum] = "cd4b8f53720fcd0c351156b840fdd15ecfbec22c951b5406ec503de49d40b9f5"


S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

do_install_append() {
    # install hosts and conf
    install -d ${D}/${sysconfdir}/ansible

    # There is no default inventory configuration installed for ansible,
    # so we use the example as a template to improve OOBE.
    install ${S}/examples/hosts ${D}/${sysconfdir}/ansible/
    install ${S}/examples/ansible.cfg ${D}/${sysconfdir}/ansible/

    # do not gather machine's information, which could reduce setup time
    sed -i "s|^#gathering = implicit|gathering = explicit|g" \
        ${D}/${sysconfdir}/ansible/ansible.cfg
}

RDEPENDS_${PN} += "python-pyyaml python-jinja2 python-modules"
