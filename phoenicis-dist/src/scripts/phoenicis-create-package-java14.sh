#!/bin/bash

## TODO: This script has not been adapted for Linux

## Dependencies
# Linux:
# - fakeroot
#

VERSION="$1"
JAVA_VERSION="$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)"


if [ "$VERSION" = "" ]; then
    echo "Warning: Version not specified. Reading from pom.xml"
    VERSION="$(cat ../../pom.xml|grep -4 '<parent>'|grep '<version>'|awk -F'[<>]' '/<version>/{print $3}')"
    echo "Using $VERSION"
fi

SCRIPT_PATH="$(dirname "$0")"
cd "$SCRIPT_PATH"
SCRIPT_PATH="$PWD"

[ "$JAVA_HOME" = "" ] && echo "Please set JAVA_HOME" && exit 0
[ ! "$JAVA_VERSION" = "14" ] && (echo "Java 14 is required to run this script" ; exit 1)
which jpackage || (echo "jpackage is required to run this script" ; exit 1)


PHOENICIS_OPERATING_SYSTEM="$(uname)"

if [ "$PHOENICIS_OPERATING_SYSTEM" == "Darwin" ]; then
    PHOENICIS_APPTITLE="Phoenicis PlayOnMac"
    JPACKAGER_OS="osx"
    JAR_RELATIVE_PATH="../Java"
    TYPE="DMG"
fi

if [ "$PHOENICIS_OPERATING_SYSTEM" == "Linux" ]; then
    PHOENICIS_APPTITLE="Phoenicis PlayOnLinux"
    JPACKAGER_OS="linux"
    JAR_RELATIVE_PATH="/usr/share/phoenicis/app"
    TYPE="DEB"
fi

PHOENICIS_TARGET="$SCRIPT_PATH/../../target"
PHOENICIS_JPACKAGER="$SCRIPT_PATH/../../target/jpackager"
PHOENICIS_RESOURCES="$SCRIPT_PATH/../resources"
PHOENICIS_MODULES="jdk.crypto.ec,java.base,javafx.base,javafx.web,javafx.media,javafx.graphics,javafx.controls,java.naming,java.sql,java.scripting,jdk.internal.vm.ci,jdk.internal.vm.compiler,org.graalvm.truffle,jdk.jsobject,jdk.xml.dom"
PHOENICIS_RUNTIME_OPTIONS="-XX:G1PeriodicGCInterval=5000 -XX:G1PeriodicGCSystemLoadThreshold=0 -XX:MaxHeapFreeRatio=10 -XX:MinHeapFreeRatio=5 -XX:-ShrinkHeapInSteps -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --upgrade-module-path=$JAR_RELATIVE_PATH/compiler.jar --module-path=../Java --add-modules=$PHOENICIS_MODULES"
PHOENICIS_JPACKAGER_ARGUMENTS=("--type" "$TYPE" "--input" "$PHOENICIS_TARGET/lib" "--main-jar" "phoenicis-javafx-$VERSION.jar" "-n" "$PHOENICIS_APPTITLE" "--dest" "$PHOENICIS_TARGET/packages/" "--add-modules" "$PHOENICIS_MODULES" "-p" "$PHOENICIS_TARGET/lib/" "--app-version" "$VERSION" "--java-options" "$PHOENICIS_RUNTIME_OPTIONS")



cd "$PHOENICIS_TARGET"

if [ "$PHOENICIS_OPERATING_SYSTEM" == "Darwin" ]; then
    rm -rf "$PHOENICIS_TARGET/packages/Phoenicis PlayOnMac.app"
    jpackage --icon "$PHOENICIS_RESOURCES/Phoenicis PlayOnMac.icns" "${PHOENICIS_JPACKAGER_ARGUMENTS[@]}"
fi

if [ "$PHOENICIS_OPERATING_SYSTEM" == "Linux" ]; then
    jpackage "${PHOENICIS_JPACKAGER_ARGUMENTS[@]}"

    packageName="Phoenicis_$VERSION"
    cd "$PHOENICIS_TARGET"
    rmdir packages/PhoenicisPlayOnLinux/
    rm -rf "packages/phoenicis" 2> /dev/null
    mv packages/Phoenicis\ PlayOnLinux/ packages/phoenicis
    rm -rf "$packageName" 2> /dev/null
    mkdir -p "$packageName/DEBIAN/"

    cat << EOF > "$packageName/DEBIAN/control"
Package: phoenicis-playonlinux
Version: $VERSION
Section: misc
Priority: optional
Architecture: all
Depends: unzip, wget, xterm | x-terminal-emulator, python, imagemagick, cabextract, icoutils, p7zip-full, curl
Maintainer: PlayOnLinux Packaging <packages@playonlinux.com>
Description: This program is a front-end for wine.
 It permits you to install Windows Games and softwares
 on Linux. It allows you to manage differents virtual hard drive,
 and several wine versions.
 Copyright 2011-2019 PlayOnLinux team <contact@playonlinux.com>
EOF

    mkdir -p $packageName/usr/share/applications
    mkdir -p $packageName/usr/share/pixmaps
    mkdir -p $packageName/usr/bin

    cp -a packages/phoenicis $packageName/usr/share/
    cp -a "$SCRIPT_PATH/../launchers/phoenicis" $packageName/usr/bin/phoenicis
    chmod +x $packageName/usr/bin/phoenicis

    cp "$SCRIPT_PATH/../resources/Phoenicis.desktop" "$packageName/usr/share/applications"
    cp "$SCRIPT_PATH/../resources/phoenicis.png" "$packageName/usr/share/pixmaps"
    cp "$SCRIPT_PATH/../resources/phoenicis-16.png" "$packageName/usr/share/pixmaps"
    cp "$SCRIPT_PATH/../resources/phoenicis-32.png" "$packageName/usr/share/pixmaps"

    fakeroot dpkg-deb --build "$packageName"
    rm -rf deb
fi
