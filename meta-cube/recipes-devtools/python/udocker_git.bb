LICENSE = "Apache-2.0"
HOMEPAGE = "https://github.com/indigo-dc/udocker"
SUMMARY = "A basic user tool to execute simple docker containers in batch or interactive systems without root privileges"
DESCRIPTION = "A basic user tool to execute simple docker containers in user space \
               without requiring root privileges. Enables download and execution of \
               docker containers by non-privileged users in Linux systems where \
               docker is not available. It can be used to pull and execute docker \
               containers in Linux batch systems and interactive clusters that are \
               managed by other entities such as grid infrastructures or externally \
               managed batch or interactive systems."
SECTION = "containers"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI = "git://github.com/indigo-dc/udocker.git;branch=master;protocol=https \
           file://0001-commands-prevent-autoinstall-by-default.patch"
SRCREV = "94fcec123257d47c4629dadd04add4247cd74f8c"

S = "${WORKDIR}/git"

DEPENDS = ""
RDEPENDS:${PN} = " \
                  python3-modules \
                  ca-certificates \
                  curl \
                 "

# custom compile rule to avoid test target which may fail when x-compiling
do_compile () {
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${S}/udocker.py ${D}/${bindir}/udocker
}
