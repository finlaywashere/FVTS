#!/bin/bash

chmod +x output/FVTS-all.jar
java -Djava.library.path=output/linux/x86_64/ -jar output/FVTS-all.jar $@
