#!/bin/sh
# Maven Wrapper script for Unix-like systems
# Downloads and runs Maven if not already cached

set -e

WRAPPER_VERSION="3.3.2"
MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-${WRAPPER_VERSION}"

if [ ! -d "$MAVEN_HOME" ]; then
    echo "Downloading Maven ${WRAPPER_VERSION}..."
    mkdir -p "$MAVEN_HOME"
    curl -fsSL "https://dlcdn.apache.org/maven/maven-3/${WRAPPER_VERSION}/binaries/apache-maven-${WRAPPER_VERSION}-bin.tar.gz" \
        | tar xz --strip-components=1 -C "$MAVEN_HOME"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
