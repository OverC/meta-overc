DEPENDS += " quota"

DEPENDS := "${@oe.utils.str_filter_out('ctdb','${DEPENDS}',d)}"
