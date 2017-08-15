#!/bin/bash

make clean
if [ ! -d "bin" ]; then
  mkdir bin
fi
#Compile the classes using make
make -f makefile
