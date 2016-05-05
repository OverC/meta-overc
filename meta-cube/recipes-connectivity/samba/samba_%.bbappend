DEPENDS += " quota"

DEPENDS := "${@oe_filter_out('ctdb','${DEPENDS}',d)}"
