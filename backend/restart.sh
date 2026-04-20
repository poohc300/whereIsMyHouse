#!/bin/bash
pkill -f "whereIsMyHouse.*spring-boot" 2>/dev/null
pkill -f "whereIsMyHouse.*gradlew" 2>/dev/null
sleep 1
./gradlew bootRun
