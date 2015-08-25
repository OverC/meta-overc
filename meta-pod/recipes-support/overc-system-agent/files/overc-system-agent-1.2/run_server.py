#!/usr/bin/python

import sys, getopt
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

@app.route('/host/rollback')
def host_rollback():
    overc=Overc.Overc()
    overc.host_rollback()
    return json_msg(overc.message)

@app.route('/host/upgrade')
def host_upgrade():
    usage = 'Usage: ' + request.url_root + 'host/upgrade?reboot=[True|False]'

    overc=Overc.Overc()
    tmp = request.args.get('reboot')
    if tmp == "True":
        print "do reboot"
        reboot = True
    elif tmp == "False":
        print "do not reboot"
        reboot = False
    else:
        return json_msg(usage)
    overc._host_upgrade(reboot)
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
    usage =  'Usage: ' + request.url_root + 'container/rollback?name=<container name>&snapshot=<snapshot name>&template=<template name>'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    snapshot = request.args.get('snapshot')
    template = request.args.get('template')
    if container_name is None or snapshot is None or template is None:
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
    usage = 'Usage: ' + request.url_root + 'container/upgrade?name=<container name>&template=<template name>'

    overc=Overc.Overc()
    container_name = request.args.get('name')
    template = request.args.get('template')
    if container_name is None or template is None:
        return json_msg(usage)
    force = True
    overc._container_update(template)
    result1 = overc.message
    overc._container_activate(container_name, template, force)
    result2 = overc.message
    result = result1 + "\n" + result2
    return json_msg(result)

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
