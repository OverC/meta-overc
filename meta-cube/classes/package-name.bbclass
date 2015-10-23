#
# package_name_hook - In order to compatible with Redhat/CentOS, which provide ${pn}-devel.
# So this function will add ${pn}-devel rprovides for each ${pn}-dev rpm packages.
#
inherit package

python package_name_hook_append() {
    """
    OE only RPROVIDES ${pn}-dev for dev rpm packages, which is not compatible with
    Redhat/CentOS, who provide ${pn}-devel. In order to build redhat/CentOS third party
    srpm packages on OE system target, add ${pn}-devel for packages who provide ${pn}-dev.
    """
    packages  = d.getVar('PACKAGES', True)

    for pkg in packages.split():

        if (pkg.endswith("-dev")):
            #
            #Some ${pn}-dev doesn't set RPROVIDES specifically, those pkgs should also
            #RPROVIDES ${pn}-devel.
            #
            if (not d.getVar('RPROVIDES_' + pkg, True)):
                d.setVar('RPROVIDES_' + pkg, " " + pkg + "el")
            else:
                #Some ${pn}-dev RPROVIDES the packages are not compatible with the 
                #Redhat/CentOS, such as alsa-lib's alsa-lib-dev PROVIDES alsa-dev,
                #but Redhat/CentOS's alsa-lib-dev PROVIDES alsa-lib-dev, instead of
                #alsa-dev, here adds the ${pn}-devel PROVIDES to every ${pn}-dev.
                if ((pkg + "el") not in list(d.getVar('RPROVIDES_' + pkg, True))):
                    d.appendVar('RPROVIDES_' + pkg, " " + pkg + "el")

            if pkg.startswith("lib") and not pkg.startswith("lib32-"):
                d.appendVar('RPROVIDES_' + pkg, " " + pkg[3:])

        for (rprov_pkg, rprov) in bb.utils.explode_dep_versions2(d.getVar('RPROVIDES_' + pkg, True) or "").iteritems():
            if rprov_pkg.endswith('-dev') and (rprov_pkg + "el") not in list(d.getVar('RPROVIDES_' + pkg, True)):
                d.appendVar('RPROVIDES_' + pkg, " " + rprov_pkg + "el")

        #
        #Some packages such as openssl, audit also produced openssl-libs 
        #and audit-libs, audit-libs-devel rpm packages, which will be buildrequired 
        #by some other packages, in order to meet this type of build requirement,
        #let the ${pn}-dev also provide ${pn}-libs ${pn}-libs-devel if both of them
        #are not provded by other rpms.
        #
        if pkg.endswith("-dev"):
            newpkg = d.getVar('PKG_' + pkg)
            #
            #Some packages will rewrite their package names by
            #changing PKG. Here also add a rprovides for this new 
            #pkg name. For an example, see debian.bbclass
            #
            if newpkg:
                provs = d.getVar('RPROVIDES_' + pkg, True)
                if newpkg+"el" not in provs:
                    d.appendVar('RPROVIDES_' + pkg, " " + newpkg+"el")
            #
            #Some packages such as openssl, audit also produced openssl-libs 
            #and audit-libs, audit-libs-devel rpm packages, which will be buildrequired 
            #by some other packages, in order to meet this type of build requirement,
            #let the ${pn}-dev also provide ${pn}-libs ${pn}-libs-devel if both of them
            #are not provded by other rpms.
            #
            if newpkg and newpkg.startswith("lib"):
                pass
            elif not pkg.endswith("lib-dev") and not pkg.endswith("libs-dev") and not pkg.startswith("lib"):
                libpkg = pkg.replace('-dev', '-libs')
                libpkgdev = pkg.replace('-dev', '-libs-devel')

                provide_pn_libs = 0
                for npkg in packages.split():
                    provides = d.getVar('RPROVIDES_' + npkg, True)
                    if (provides and libpkg in provides):
                        provide_pn_libs = 1

                if not provide_pn_libs:
                    d.appendVar('RPROVIDES_' + pkg, " " + libpkg)
                    d.appendVar('RPROVIDES_' + pkg, " " + libpkgdev)

        bb.debug(1, 'RPROVIDES: pkg %s rprovides: %s' % (pkg, str(d.getVar('RPROVIDES_' + pkg, True))))
}
