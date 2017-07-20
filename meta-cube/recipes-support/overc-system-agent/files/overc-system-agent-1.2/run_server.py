#!/usr/bin/python

import sys, getopt, os, urllib2
import Overc

from flask import Flask
from flask import jsonify
from flask import request
app = Flask(__name__)

def json_msg(s):
    message = {}

    message['result'] = s.replace("\n", ";");
    resp = jsonify(message)
    return resp

@app.route('/system/rollback')
def system_rollback():
    usage = 'Usage: ' + request.url_root + 'system/rollback?template=[dom0]'
    overc=Overc.Overc()
    template = request.args.get('template')

    if template != 'dom0':
        usage += "\n The only supported template is 'dom0'"
        return json_msg(usage)

    print "System will rollback and reboot!"
    overc._system_rollback(template)

@app.route('/system/upgrade')
def system_upgrade():
    usage = 'Usage: ' + request.url_root + 'system/upgrade?template=[dom0]&reboot=[True|False]&force=[True|False]'
    overc=Overc.Overc()
    reboot_s = request.args.get('reboot')
    force_s = request.args.get('force')
    template = request.args.get('template')
    reboot=False
    force=False
    skipscan=True
    skip_del=False
    
    if template != 'dom0':
        usage += "\n The only supported template is 'dom0'"
        return json_msg(usage)

    if reboot_s == "True":
        print "do reboot"
    if force_s == "True":
        print "force upgrade"
        force=True
    overc._system_upgrade(template, reboot, force, skipscan, skip_del)
    return json_msg(overc.message)

@app.route('/host/rollback')
def host_rollback():
    overc=Overc.Overc()
    overc.host_rollback()
    return json_msg(overc.message)

@app.route('/host/upgrade')
def host_upgrade():
    usage = 'Usage: ' + request.url_root + 'host/upgrade?reboot=[True|False]&force=[True|False]'

    overc=Overc.Overc()
    reboot_s = request.args.get('reboot')
    force_s = request.args.get('force')
    reboot=False
    force=False
    if reboot_s == "True":
        print "do reboot"
        reboot = True
    if force_s == "True":
        print "do force to upgrade"
	force=True

    overc._host_upgrade(reboot, force)
    return json_msg(overc.message)

@app.route('/host/update')
def host_update():
    overc=Overc.Overc()
    overc.host_update()
    return json_msg(overc.message)

@app.route('/host/newer')
def host_newer():
    overc=Overc.Overc()
    overc.host_newer()
    return json_msg(overc.message)

@app.route('/container/rollback')
def container_rollback():
    usage =  'Usage: ' + request.url_root + 'container/rollback?name=<container name>&snapshot=<snapshot name>&template=<template name> [snapshot optional]'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    snapshot = request.args.get('snapshot')
    template = request.args.get('template')
    if container_name is None or template is None:
        return json_msg(usage)
    overc._container_rollback(container_name, snapshot, template)
    return json_msg(overc.message)

@app.route('/container/update')
def container_update():
    usage = 'Usage: ' + request.url_root + 'container/update?template=<template name>'

    overc=Overc.Overc()
    template = request.args.get('template')
    if template is None:
        return json_msg(usage)
    overc._container_update(template)
    return json_msg(overc.message)

@app.route('/container/list')
def container_list():
    usage = 'Usage: ' + request.url_root + 'container/list?template=<template name>'
    overc=Overc.Overc()
    template = request.args.get('template')
    if template is None:
        return json_msg(usage)
    overc._container_list(template)
    return json_msg(overc.message)

@app.route('/container/snapshot')
def container_snapshot():
    usage = 'Usage: ' + request.url_root + 'container/snapshot?name=<container name>&template=<template name>'
    overc=Overc.Overc()
    template = request.args.get('template')
    container_name = request.args.get('name')
    if template is None or container_name is None:
        return json_msg(usage)

    overc._container_snapshot(container_name, template)
    return json_msg(overc.message)

@app.route('/container/list_snapshots')
def container_list_snapshots():
    usage = 'Usage: ' + request.url_root + 'container/list_snapshots?name=<container name>&template=<template name>'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    template = request.args.get('template')
    if container_name is None or template is None:
        return json_msg(usage)
    overc._container_snapshot_list(container_name, template)
    return json_msg(overc.message)

@app.route('/container/send_image')
def container_send_image():
    usage = 'Usage: ' + request.url_root + 'container/send_image?url=<image url>&template=<template name>'

    overc=Overc.Overc()
    url = request.args.get('url')
    template = request.args.get('template')
    if url is None or template is None:
        return json_msg(usage)
    template_list = os.listdir("/etc/overc/container")
    if template not in template_list:
        usage += "\n The template name is not valid"
        return json_msg(usage)

    req = urllib2.Request(url)
    req.get_method = lambda: 'HEAD'
    try:
        status = urllib2.urlopen(req)
    except Exception,e:
        usage += "\n The image url is not valid"
        return json_msg(usage)

    re_code = status.getcode()
    if ((re_code != None) and (re_code != 200)):
        usage += "\n The image url is not valid, http status code is: %s" % re_code
        return json_msg(usage)

    overc._container_send_image(template, url)
    return json_msg(overc.message)

@app.route('/container/activate')
def container_activate():
    usage = 'Usage: ' + request.url_root + 'container/activate?name=<container name>&template=<template name>'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    template = request.args.get('template')
    if container_name is None or template is None:
        return json_msg(usage)
    force = True
    overc._container_activate(container_name, template, force)
    return json_msg(overc.message)

@app.route('/container/start')
def container_start():
    usage = 'Usage: ' + request.url_root + 'container/start?name=<container name>&template=<template name>'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    template = request.args.get('template')
    if container_name is None or template is None:
        return json_msg(usage)
    overc._container_start(container_name, template)
    return json_msg(overc.message)

@app.route('/container/stop')
def container_stop():
    usage = 'Usage: ' + request.url_root + 'container/stop?name=<container name>&template=<template name>'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    template = request.args.get('template')
    if container_name is None or template is None:
        return json_msg(usage)
    overc._container_stop(container_name, template)
    return json_msg(overc.message)

@app.route('/container/upgrade')
def container_upgrade():
    usage = 'Usage: ' + request.url_root + 'container/upgrade?name=<container name>&template=<template name>&rpm=yes|no&image=yes|no'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    template = request.args.get('template')
    rpm = request.args.get('rpm')
    image = request.args.get('image')

    if container_name is None or template is None:
        return json_msg(usage)
    if rpm is None or rpm == 'no':
        rpm_upgrade = False
    elif rpm == 'yes':
        rpm_upgrade = True
    elif rpm != 'no':
        return json_msg(usage)

    if image is None or image == 'no':
        image_upgrade = False
    elif image == 'yes':
        image_upgrade = True
    elif image != 'no':
        return json_msg(usage)

    overc._container_upgrade(container_name, template, rpm_upgrade, image_upgrade)
    return json_msg(overc.message)

@app.route('/container/delete')
def container_delete():
    usage = 'Usage: ' + request.url_root + 'container/delete?name=<container name>&template=<template name>'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    template = request.args.get('template')
    if container_name is None or template is None:
        return json_msg(usage)
    force = True
    overc._container_delete(container_name, template, force)
    return json_msg(overc.message)

@app.route('/container/delete_snapshots')
def container_delete_snapshots():
    usage = 'Usage: ' + request.url_root + 'container/delete_snapshots?name=<container name>&template=<template name>'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    template = request.args.get('template')
    if container_name is None or template is None:
        return json_msg(usage)
    overc._container_delete_snapshots(container_name, template)
    return json_msg(overc.message)

if __name__ == '__main__':
    default_port = 5555
    try:
        opts, args = getopt.getopt(sys.argv[1:],"hdp::",["port="])
    except getopt.GetoptError:
        print sys.argv[0],' [-d] [-p <port>]'
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print sys.argv[0],' [-d] [-p <port>]'
            sys.exit()
        elif opt in ("-p", "--port"):
            try:
                default_port = int(arg)
            except ValueError:
                print sys.argv[0],' -p <port>'
                sys.exit(2)
        elif opt == '-d':
            app.debug = True
    app.run(port=default_port, host='0.0.0.0')
