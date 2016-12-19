#------------------------------------------------------------------------------
# WAFTOOLS generated makefile
# version: 0.4.13
# waf: 1.8.19
#------------------------------------------------------------------------------

SHELL=/bin/sh

# commas, spaces and tabs:
sp:= 
sp+= 
tab:=$(sp)$(sp)$(sp)$(sp)
comma:=,

#------------------------------------------------------------------------------
# definition of build and install locations
#------------------------------------------------------------------------------

#ifeq ($(TOP),)
TOP=$(CURDIR)
OUT=$(TOP)/build
#else
#OUT:=$(subst $(sp),/,$(call rptotop) build $(call rpofcomp))
#endif


PREFIX?=$(HOME)
BINDIR?=$(PREFIX)/bin
LIBDIR?=$(PREFIX)/lib

#------------------------------------------------------------------------------
# component data
#------------------------------------------------------------------------------
BIN=pflask
OUTPUT=$(OUT)/$(BIN)

# REMARK: use $(wildcard src/*.c) to include all sources.
SOURCES= \
	src/cgroup.c \
	src/cmdline.c \
	src/dev.c \
	src/mount.c \
	src/netif.c \
	src/nl.c \
	src/path.c \
	src/pflask.c \
	src/printf.c \
	src/pty.c \
	src/sync.c \
	src/user.c \
	src/util.c

ifneq ($(findstring HAVE_DBUS,$(DEFINES)),)
SOURCES+=src/machine.c
endif

OBJECTS=$(SOURCES:.c=.1.o)

override DEFINES+=_GNU_SOURCE
override DEFINES:=$(addprefix -D,$(DEFINES))

INCLUDES+= \


HEADERS:=$(foreach inc,$(INCLUDES),$(wildcard $(inc)/*.h))
INCLUDES:=$(addprefix -I,$(INCLUDES))

CFLAGS+=

LINKFLAGS+=

RPATH+=
RPATH:= $(addprefix -Wl$(comma)-rpath$(comma),$(RPATH))

LIBPATH_ST+=
LIBPATH_ST:= $(addprefix -L,$(LIBPATH_ST))

LIB_ST+=
LIB_ST:= $(addprefix -l,$(LIB_ST))

LIBPATH_SH+=
LIBPATH_SH:= $(addprefix -L,$(LIBPATH_SH))

LINK_ST= -Wl,-Bstatic $(LIBPATH_ST) $(LIB_ST)

LIB_SH+=
LIB_SH:= $(addprefix -l,$(LIB_SH))

LINK_SH= -Wl,-Bdynamic $(LIBPATH_SH) $(LIB_SH)

#------------------------------------------------------------------------------
# returns the relative path of this component from the top directory
#------------------------------------------------------------------------------
define rpofcomp
$(subst $(subst ~,$(HOME),$(TOP))/,,$(CURDIR))
endef

#------------------------------------------------------------------------------
# returns the relative path of this component to the top directory
#------------------------------------------------------------------------------
define rptotop
$(foreach word,$(subst /,$(sp),$(call rpofcomp)),..)
endef

#------------------------------------------------------------------------------
# define targets
#------------------------------------------------------------------------------
commands= build clean install uninstall all

.DEFAULT_GOAL=all

#------------------------------------------------------------------------------
# definitions of recipes (i.e. make targets)
#------------------------------------------------------------------------------
all: build

build: $(OBJECTS)
	$(CC) $(LINKFLAGS) $(addprefix $(OUT)/,$(OBJECTS)) -o $(OUTPUT) $(RPATH) $(LINK_ST) $(LINK_SH)

clean:
	$(foreach obj,$(OBJECTS),rm -f $(OUT)/$(obj);)
	rm -f $(OUTPUT)

install: build
	mkdir -p $(BINDIR)
	cp $(OUTPUT) $(BINDIR)

uninstall:
	rm -f $(BINDIR)/$(BIN)

$(OBJECTS): $(HEADERS)
	mkdir -p $(OUT)/$(dir $@)
	$(CC) $(CFLAGS) $(INCLUDES) $(DEFINES) $(subst .1.o,.c,$@) -c -o $(OUT)/$@

.PHONY: $(commands)

