SUMMARY = "Application container for ${C3_APP_CONTAINER_APP}"
DESCRIPTION = "A small application container which will run \
                ${C3_APP_CONTAINER_APP}."
HOMEPAGE = "http://www.windriver.com"

require c3-app-container.inc

IMAGE_INSTALL += "${C3_APP_CONTAINER_APP}"
