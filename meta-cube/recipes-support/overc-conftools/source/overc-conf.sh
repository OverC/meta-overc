#!/bin/bash

cd /etc/overc-conf/overc-conf.d
for script in *.sh; do
    (./$script 2>/dev/null)
done
cd - >/dev/null
