#!/bin/bash
./gradlew build -PbuiltType=linuxraspbian -PtestType=windowsx86-64 $@
