DEPENDS += " quota"

DEPENDS := "${@oe_filter_out('ctdb','${DEPENDS}',d)}"

FILES_${PN} += " ${libdir}/security/pam_winbind.so \
	${baselibdir}/security/pam_winbind.so \
	"
