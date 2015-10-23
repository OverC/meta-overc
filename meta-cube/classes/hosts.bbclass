# TARGETNAME can be overridden

ROOTFS_PREPROCESS_COMMAND += "builder_set_hostname ; "

python builder_set_hostname() {
    targetname = d.getVar("TARGETNAME", True)
    if targetname != None:
        return

    status, date = oe.utils.getstatusoutput("date +%d-%m-%y")
    if status:
        bb.warn("Can't get the date string for target hostname")

    targetname = "cube-%s" %  date
    d.setVar("TARGETNAME", targetname)
}
