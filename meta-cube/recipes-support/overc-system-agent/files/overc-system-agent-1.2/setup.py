#!/usr/bin/env python3

# Author: Fupan Li <fupan.li@windriver.com>, Amy Fong <amy.fong@windriver.com>
import os
from distutils.core import setup

packages = [
            "Overc",
            "Overc.backends"
           ]

setup(name = "overc", 
      scripts=["overc"], 
      version="1.2", 
      description="OverC Management Tool", 
      author="Fupan Li, Amy Fong", 
      author_email="fupan.li@windriver.com, amy.fong@windriver.com",
      packages=packages
     )
