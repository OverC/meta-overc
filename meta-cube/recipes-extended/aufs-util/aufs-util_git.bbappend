do_compile:class-native () {
    oe_runmake tools CPPFLAGS="-I${S}/include -I${S}/libau" CC="${BUILD_CC}"
}
