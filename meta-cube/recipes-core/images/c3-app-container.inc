SUMMARY ?= "Sample application container"
DESCRIPTION ?= "A small application container which will run \
                the application defined in IMAGE_INSTALL."
HOMEPAGE ?= "http://www.windriver.com"

LICENSE ?= "MIT"
LIC_FILES_CHKSUM ?= "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES:remove = "live"

TARGETNAME ?= "c3-app-container"

IMAGE_INSTALL += ""

IMAGE_FEATURES = ""

inherit image
