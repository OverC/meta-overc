# We need to load the overc distro components, only if overc is out distro
# Since we don't know the distro during layer.conf load time, we delay using a
# special bbclass that simple includes the CUBE_CONFIG_PATH file.

include ${CUBE_CONFIG_PATH}
