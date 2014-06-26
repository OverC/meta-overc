#!/bin/bash

echo $* |grep -q -- -m32

if [ $? != 0 ]; then
	x86_64-poky-linux-gcc "$@"
else
	i586-pokymllib32-linux-gcc "$@"
fi	
