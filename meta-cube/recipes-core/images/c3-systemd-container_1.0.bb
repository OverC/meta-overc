SUMMARY = "Systemd system container for ${C3_SYSTEMD_CONTAINER_APP}"
DESCRIPTION = "A small systemd system container which will run \
                ${C3_SYSTEMD_CONTAINER_APP}."
HOMEPAGE = "http://www.windriver.com"


# Use local.conf to specify the application(s) to install
IMAGE_INSTALL = "${C3_SYSTEMD_CONTAINER_APPS}"

# Use local.conf to specify additional systemd services to disable. To overwrite
# the default list use SERVICES_TO_DISABLE_pn-c3-systemd-container in local.conf
SERVICES_TO_DISABLE_append += "${C3_SYSTEMD_CONTAINER_DISABLE_SERVICES}"

# Use local.conf to enable systemd services
SERVICES_TO_ENABLE += "${C3_SYSTEMD_CONTAINER_ENABLE_SERVICES}"

inherit c3-systemd-container
