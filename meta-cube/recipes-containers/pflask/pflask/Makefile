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

# token for separating dictionary keys and values:
dsep:=;

# token for separating list elements:
lsep:=,

# token for joining command and component names (e.g. 'build.hello')
csep:=.

export APPNAME:=pflask
export APPVERSION:=0.2
export PREFIX:=/usr/local
export TOP:=$(CURDIR)
export OUT:=
export AR:=/usr/bin/ar
export CC:=/usr/bin/gcc
export CXX:=g++
export CFLAGS+=-Wall -Wextra -pedantic -g -std=gnu99
export CXXFLAGS:=
export DEFINES:=_GNU_SOURCE
export RPATH:=
export BINDIR:=$(PREFIX)/bin
export LIBDIR:=$(PREFIX)/lib
export INCLUDES:=deps src
export LINKFLAGS:=

override DEFINES+=_GNU_SOURCE

SEARCHPATH=components/
SEARCHFILE=Makefile

#------------------------------------------------------------------------------
# list of unique logical module names;
modules= \
	pflask

# dictionary of modules names (key) and paths to modules;
paths= \
	pflask;.

# dictionary of modules names (key) and module dependencies;
deps= \
	pflask;

#------------------------------------------------------------------------------
# define targets
#------------------------------------------------------------------------------
build_targets=$(addprefix build$(csep),$(modules))
clean_targets=$(addprefix clean$(csep),$(modules))
install_targets=$(addprefix install$(csep),$(modules))
uninstall_targets=$(addprefix uninstall$(csep),$(modules))

cmds=build clean install uninstall
commands=$(sort $(cmds) all help find list modules $(foreach prefix,$(cmds),$($(prefix)_targets)))

.DEFAULT_GOAL:=all

#------------------------------------------------------------------------------
# recursive wild card implementation
#------------------------------------------------------------------------------
define rwildcard
$(wildcard $1$2) $(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2))
endef

#------------------------------------------------------------------------------
# returns the value from a dictionary
# $1 = key, where key is the functional name of the component.
# $2 = dictionary
#------------------------------------------------------------------------------
define getdval
$(subst $(lastword $(subst $(csep),$(sp),$1))$(dsep),$(sp),$(filter $(lastword $(subst $(csep),$(sp),$1))$(dsep)%,$2))
endef

#------------------------------------------------------------------------------
# returns path to makefile
# $1 = key, where key is the functional name of the component.
#------------------------------------------------------------------------------
define getpath
$(call getdval, $1, $(paths))
endef

#------------------------------------------------------------------------------
# returns component dependencies.
# $1 = key, where key is the functional name of the component.
#------------------------------------------------------------------------------
define getdeps
$(addprefix $(firstword $(subst $(csep),$(sp),$1))$(csep),$(subst $(lsep),$(sp),$(call getdval, $1, $(deps))))
endef

#------------------------------------------------------------------------------
# creates a make recipe:
#      'make -r -C <path> -f <name>.mk <command>'
# where:
#      <path>     is the relative path to the component
#      <name>     is the name of the component
#      <command>  is the make action to be executed, e.g. build, install, clean
#
# $1 = key, where key is the functional recipe name (e.g. build.a).
#------------------------------------------------------------------------------
define domake
$1: $(call getdeps, $1)
	$(MAKE) -r -C $(call getpath,$1) -f $(lastword $(subst $(csep),$(sp),$1)).mk $(firstword $(subst $(csep),$(sp),$1))
endef

#------------------------------------------------------------------------------
# return files found in given search path
# $1 = search path
# $2 = file name so search
#------------------------------------------------------------------------------
define dofind
$(foreach path, $(dir $(call rwildcard,$1,$2)),echo "  $(path)";)
endef

#------------------------------------------------------------------------------
# definitions of recipes (i.e. make targets)
#------------------------------------------------------------------------------
all: build

build: $(build_targets)

clean: $(clean_targets)

install: $(install_targets)

uninstall: $(uninstall_targets)

list:
	@echo ""
	@$(foreach cmd,$(commands),echo "  $(cmd)";)
	@echo ""

modules:
	@echo ""
	@$(foreach module,$(modules),echo "  $(module)";)
	@echo ""

find:
	@echo ""
	@echo "$@:"
	@echo "  path=$(SEARCHPATH) file=$(SEARCHFILE)"
	@echo ""
	@echo "result:"
	@$(call dofind,$(SEARCHPATH),$(SEARCHFILE))
	@echo ""

help:
	@echo ""
	@echo "$(APPNAME) version $(APPVERSION)"
	@echo ""
	@echo "usage:"
	@echo "  make [-r] [-s] [--jobs=N] [command] [VARIABLE=VALUE]"
	@echo ""
	@echo "commands:"
	@echo "  all                                 builds all modules"
	@echo "  build                               builds all modules"
	@echo "  build$(csep)a                       builds module 'a' and it's dependencies"
	@echo "  clean                               removes all build intermediates and outputs"
	@echo "  clean$(csep)a                       cleans module 'a' and it's dependencies"
	@echo "  install                             installs files in $(PREFIX)"
	@echo "  install$(csep)a                     installs module 'a' and it's dependencies"
	@echo "  uninstall                           removes all installed files from $(PREFIX)"
	@echo "  uninstall$(csep)a                   removes module 'a' and it's dependencies"
	@echo "  list                                list available make commands (i.e. recipes)"
	@echo "  modules                             list logical names of all modules"
	@echo "  find [SEARCHPATH=] [SEARCHFILE=]    searches for files default(path=$(SEARCHPATH),file=$(SEARCHFILE))"
	@echo "  help                                displays this help message."
	@echo ""
	@echo "remarks:"
	@echo "  use options '-r' and '--jobs=N' in order to improve speed"
	@echo "  use options '-s' to decrease verbosity"
	@echo ""

$(foreach t,$(build_targets),$(eval $(call domake,$t)))

$(foreach t,$(clean_targets),$(eval $(call domake,$t)))

$(foreach t,$(install_targets),$(eval $(call domake,$t)))

$(foreach t,$(uninstall_targets),$(eval $(call domake,$t)))

.PHONY: $(commands)

