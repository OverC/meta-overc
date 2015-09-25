import sys, os
import subprocess
from Overc.package import Package
from Overc.container import Container
from Overc.utils import ROOTMOUNT

def Is_btrfs():
    return not os.system('btrfs subvolume show %s >/dev/null 2>&1' % ROOTMOUNT)

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
            print "Error: Not using a supported filesystem!"
            sys.exit(1)
        self.agency = Host_update()
        self.package = Package()
        self.container = Container()
        
        if not self.agency:
            print "Error: cannot get the right backends!"
            sys.exit(2)

        self.bakup_mode = self.agency.bakup_mode

    def help(self):
        if os.path.exists("/usr/bin/smart"):
            return _('OverC Management Tool for Host update')

    def set_args(self, args):
        self.args=args

        try:
            self.command = args.command
        except:
            self.command = None

    def _need_upgrade(self):
        self.host_update()
        argv = "newer"
        if self.package._smartpm(argv) == 0 or self.args.force:
            return True
        else:
            return False

    def host_status(self):
        print "host status"

    def host_newer(self):
        argv = "newer"
        self.host_update()
        tmp1 = self.package.message
        self.package._smartpm(argv)
        tmp2 = self.package.message
        self.message = tmp1 + "\n" + tmp2
        print self.message

    def host_update(self):
        argv = "update"
        self.package._smartpm(argv)
        self.message = self.package.message
        print self.message
       
    def host_upgrade(self):
        self._host_upgrade(self.args.reboot)
        print self.message
    def _host_upgrade(self, reboot):
        if self._need_upgrade():
            self.agency.do_upgrade()
            self.message = self.agency.message
        else:
            self.message = "There is no new system available to upgrade!"
       
        if reboot:
            # os.system('reboot')
            self.message += "\nrebooting..."

    def host_rollback(self):
        if self.bakup_mode:
            self.message = "Error: You are running in the backup mode, cannot do rollback!"
            print self.message
            return

        print "host rollback"
        r = self.agency.do_rollback()
        self.message = self.agency.message
        print self.message
        if r:
            os.system('reboot')
        
    def container_rollback(self):
        self._container_rollback(self.args.name, self.args.snapshot_name, self.args.template)
        print self.message
        sys.exit(self.retval)
    def _container_rollback(self, container, snapshot, template):
        self.retval = self.container.rollback(container, snapshot, template)
        self.message = self.container.message

    def container_list(self):
        self._container_list(self.args.template)
        print self.message
        sys.exit(self.retval)
    def _container_list(self, template):
        self.retval = self.container.list(template)
        self.message = self.container.message

    def container_snapshot_list(self):
        self._container_snapshot_list(self.args.name, self.args.template)
        print self.message
        sys.exit(self.retval)
    def _container_snapshot_list(self, container, template):
        self.retval = self.container.list_snapshot(container, template)
        self.message = self.container.message

    def container_activate(self):
        self._container_activate(self.args.name, self.args.template, self.args.force)
        print self.message
        sys.exit(self.retval)
    def _container_activate(self, container, template, force):
        self.retval = self.container.activate(container, template, force)
        self.message = self.container.message

    def container_start(self):
        self._container_start(self.args.name, self.args.template)
        print self.message
        sys.exit(self.retval)
    def _container_start(self, container, template):
        self.retval = self.container.start(container, template)
        self.message = self.container.message

    def container_stop(self):
        self._container_stop(self.args.name, self.args.template)
        print self.message
        sys.exit(self.retval)
    def _container_stop(self, container, template):
        self.retval = self.container.stop(container, template)
        self.message = self.container.message

    def container_update(self):
        self._container_update(self.args.template)
        print self.message
        sys.exit(self.retval)
    def _container_update(self, template):
        self.retval = self.container.update(template)
        self.message = self.container.message

    def container_send_image(self):
        self._container_send_image(self.args.template, self.args.image_url)
        print self.message
        sys.exit(self.retval)
    def _container_send_image(self, template, url):
        self.retval = self.container.send_image(template, url)
        self.message = self.container.message

