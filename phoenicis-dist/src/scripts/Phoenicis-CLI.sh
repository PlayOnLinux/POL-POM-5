#!/usr/bin/env bash
CLASSPATH=${CLASSPATH}:/usr/local/lib/phoenicis/*

java -classpath "$CLASSPATH" com.playonlinux.cli.PlayOnLinuxCLI "$@"
