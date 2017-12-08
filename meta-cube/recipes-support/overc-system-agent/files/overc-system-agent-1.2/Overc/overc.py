import sys, os
import subprocess
from Overc.package import Package
from Overc.container import Container
from Overc.utils import Utils
from Overc.logger import logger as log

def Is_btrfs():
    return not os.system('cube-cmd btrfs subvolume show / >/dev/null 2>&1')

def Is_lvm():
    #to be done
    return False

if Is_btrfs():
    from Overc.backends.btrfs import Btrfs as Host_update

#A placeholder for dealing with other supported filesystem's update, such as LVM etc.
elif Is_lvm():
    pass
else:
    Host_update = None

class Overc(object):
    def __init__(self):
        if Host_update is None:
            log.error("Not using a supported filesystem!")
            sys.exit(1)
        self.agency = Host_update()
        self.package = Package()
        self.container = Container()
        self.utils = Utils()
        self.message = ""
        
        if not self.agency:
            log.error("cannot get the right backends!")
            sys.exit(2)

        self.bakup_mode = self.agency.bakup_mode

    def help(self):
        if os.path.exists("/usr/bin/dnf"):
            return _('OverC Management Tool for Host update')

    def set_args(self, args):
        self.args=args

        try:
            self.command = args.command
        except:
            self.command = None

    def system_upgrade(self):
        self._system_upgrade(self.args.template, self.args.reboot, self.args.force, self.args.skipscan, self.args.skip_del)

    def _system_upgrade(self, template, reboot, force, skipscan, skip_del):
        containers = self.container.get_container(template)
        overlay_flag = 0

        # By now only support "Pulsar" and "overc" Linux upgrading
        DIST = "Pulsar overc"
        succeeded = []
        for cn in containers:
            if self.container.is_active(cn, template):
                for dist in DIST.split():
                    if dist in self.container.get_issue(cn, template).split():
                        log.info("Updating container %s" % cn)
                        self._container_upgrade(cn, template, True, False, skip_del) #by now only rpm upgrade support
                        if self.retval is not 0:
                            log.error("*** Failed to upgrade container %s" % cn)
                            log.error("*** Abort the system upgrade action")
                            break
                        else:
                            succeeded.append(cn)
                            if self.container.is_overlay(cn) > 0:
                                overlay_flag = 1
                        break
            if self.retval is not 0:
                break

        if self.retval is not 0:
            for cn in succeeded:
                self._container_rollback(cn, None, template, True)
            sys.exit(self.retval)

        rc = self._host_upgrade(0, force)

        if ((overlay_flag == 1) and (skipscan == 0) and (rc == 1)):
            # Enable lxc-overlay service in essential by create a flagfile in CONTAINER_MOUNT
            lxcfile = '%s/need_scan_duplicate' % (CONTAINER_MOUNT)
            lxc = open(lxcfile, 'w+')
            lxc.close()

        if ((rc == 1) and (reboot != 0)):
            log.info("rebooting...")
            os.system('reboot')

    def system_rollback(self):
        containers = self.container.get_container(self.args.template)

        # By now only support "Pulsar" and "overc" Linux rollback
        DIST = "Pulsar overc"
        need_reboot=False
        for cn in containers:
            if self.container.is_active(cn, self.args.template):
                for dist in DIST.split():
                    if dist in self.container.get_issue(cn, self.args.template).split():
                        log.info("Rollback container %s" % cn)
                        self._container_rollback(cn, None, self.args.template, True)
                        if self.retval is not 0:
                            log.error("*** Failed to rollback container %s" % cn)
                        else:
                            need_reboot=True
                        break


        self.host_rollback()
        if need_reboot:
            log.info("rebooting...")
            os.system('reboot')

    def system_cleanup(self):
        self.agency.clean_essential()
        self.container_cleanup()

    def factory_reset(self):
        rc = self.agency.factory_reset()
        if not rc:
           self.agency.clean_essential()
           self.agency.clean_container()
           self.message += self.agency.message
        else:
            log.info("rebooting...")
            os.system('reboot')
                        
    def _need_upgrade(self):
        if self.host_newer() == 100:
            return True
        else:
            return False

    def host_status(self):
        log.info("host status")

    def host_newer(self):
        rc = 0
        try:
            self.message += subprocess.check_output("cube-cmd dnf check-update --refresh", stderr=subprocess.STDOUT, shell=True).decode("utf-8").strip("\n")
        except subprocess.CalledProcessError as e:
            rc = e.returncode
            self.message += e.output.decode("utf-8").strip()

        if rc == 100 or rc == 0:
            log.info(self.message)
        else:
            log.error(self.message)
        return rc

    def host_update(self):
        rc = 0
        try:
            self.message += subprocess.check_output("cube-cmd dnf updateinfo --refresh", stderr=subprocess.STDOUT, shell=True).decode("utf-8").strip("\n")
        except subprocess.CalledProcessError as e:
            rc = e.returncode
            self.message += e.output.decode("utf-8").strip()

        if rc == 0:
            log.info(self.message)
        else:
            log.error(self.message)
        return rc

    def host_upgrade(self):
        self._host_upgrade(self.args.reboot, self.args.force)

    def _host_upgrade(self, reboot, force):
        rc = False
        if self._need_upgrade() or force:
            rc = self.agency.do_upgrade()
            self.message = self.agency.message
        else:
            self.message = "There is no new system available to upgrade!"
            log.info(self.message)
            return True

        if reboot:
            log.info("rebooting...")
            os.system('reboot')
        return rc

    def host_rollback(self):
        if self.bakup_mode:
            self.message = "You are running in the backup mode, cannot do rollback!"
            log.error(self.message)
            return False

        log.info("Start doing host rollback")
        r = self.agency.do_rollback()
        self.message = self.agency.message
        if r:
            os.system('reboot')
        else:
            log.error(self.message)
            return False
        
    def container_rollback(self):
        self._container_rollback(self.args.name, self.args.snapshot_name, self.args.template, True)
        sys.exit(self.retval)

    def _container_rollback(self, container, snapshot, template, force):
        self.retval = self.container.rollback(container, snapshot, template, force)
        self.message = self.container.message

    def container_list(self):
        self._container_list(self.args.template)
        sys.exit(self.retval)

    def _container_list(self, template):
        self.retval = self.container.list(template)
        self.message = self.container.message

    def container_snapshot_list(self):
        self._container_snapshot_list(self.args.name, self.args.template)
        sys.exit(self.retval)

    def _container_snapshot_list(self, container, template):
        self.retval = self.container.list_snapshot(container, template)
        self.message = self.container.message

    def container_snapshot(self):
        self._container_snapshot(self.args.name, self.args.template)
        sys.exit(self.retval)

    def _container_snapshot(self, container, template):
        self.retval = self.container.snapshot(container, template)
        self.message = self.container.message

    def container_activate(self):
        self._container_activate(self.args.name, self.args.template, self.args.force)
        sys.exit(self.retval)

    def _container_activate(self, container, template, force):
        self.retval = self.container.activate(container, template, force)
        self.message = self.container.message

    def container_start(self):
        self._container_start(self.args.name, self.args.template)
        sys.exit(self.retval)

    def _container_start(self, container, template):
        self.retval = self.container.start(container, template)
        self.message = self.container.message

    def container_stop(self):
        self._container_stop(self.args.name, self.args.template)
        sys.exit(self.retval)

    def _container_stop(self, container, template):
        self.retval = self.container.stop(container, template)
        self.message = self.container.message

    def container_update(self):
        self._container_update(self.args.template)
        sys.exit(self.retval)

    def _container_update(self, template):
        self.retval = self.container.update(template)
        self.message = self.container.message

    def container_send_image(self):
        self._container_send_image(self.args.template, self.args.image_url)
        sys.exit(self.retval)

    def _container_send_image(self, template, url):
        self.retval = self.container.send_image(template, url)
        self.message = self.container.message

    def container_delete(self):
        self._container_delete(self.args.name, self.args.template, self.args.force)
        sys.exit(self.retval)

    def _container_delete(self, container, template, force):
        self.retval = self.container.delete(container, template, force)
        self.message = self.container.message

    def container_upgrade(self):
        # Perform overlay check fist
        overlaylist = self.container.get_overlay(self.args.name)
        if len(overlaylist)>0:
            log.info("Container %s has overlayed dir, including" % self.args.name)
            log.info(overlaylist)
            log.info("This container can only be upgraded via a system upgrade")
            self.retval = 0
        else:
            self._container_upgrade(self.args.name, self.args.template, self.args.rpm, self.args.image, self.args.skip_del)
        sys.exit(self.retval)

    def _container_upgrade(self, container, template, rpm=True, image=False, skip_del=False):
        self.retval = self.container.upgrade(container, template, rpm, image, skip_del)
        self.message = self.container.message

    def container_delete_snapshots(self):
        self._container_delete_snapshots(self.args.name, self.args.template)
        sys.exit(self.retval)

    def container_cleanup(self):
        # clean up temporary snapshots
        self.agency.clean_container()

        # TODO: extend this list according to the supported templates
        templates = ['dom0']
        for template in templates:
            for container in self.container.get_container(template):
                self._container_delete_snapshots(container, template)

    def _container_delete_snapshots(self, container, template):
        self.retval = self.container.delete_snapshots(container, template)
        self.message = self.container.message

    def container_overlay(self):
        # Parser commnand, create or restore
        if self.args.ollist:
            self._container_overlay_list(self.args.name)
        elif self.args.olstop:
            self._container_overlay_stop(self.args.name, self.args.oldir)
        else :
            self._container_overlay_create(self.args.name, self.args.oldir, self.args.olsource)

    def _container_overlay_list(self, container):
        # List overlay dir in container
        log.info("overlayed directories in %s including:" % container)
        log.info(",".join(self.container.get_overlay(container)))

    def _container_overlay_create(self, container, dirs, source):
        # Create overlay dir in container
        self.retval = self.container.overlay_create(container, dirs, source)

    def _container_overlay_stop(self, container, dirs):
        # Restore overlay-ed dir
        self.retval = self.container.overlay_stop(container, dirs)
