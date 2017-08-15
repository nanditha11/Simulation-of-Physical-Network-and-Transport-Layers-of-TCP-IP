#!/bin/bash

#Compile
sh compile.sh

#Starts main
java -cp ./bin edu.fit.cs.cn.main.FPMain $1
