DESCRIPTION = "Basic and Digest HTTP authentication for Flask routes"
HOMEPAGE = "http://github.com/miguelgrinberg/flask-httpauth/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRCNAME = "Flask-HTTPAuth"

SRC_URI = "https://github.com/miguelgrinberg/${SRCNAME}/archive/v${PV}.tar.gz"

SRC_URI[md5sum] = "22d114acb99ebdb9e75d77387c579d2f"
SRC_URI[sha256sum] = "c027cb75ff69751ecbe75ee1225a2bdd6bc9e4f3ffb15ecab8b0934b5dddc1f1"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
