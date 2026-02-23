#!/bin/bash

VERSION=$1
PATCH=$2

if [ -z "$VERSION" ]; then
  echo "Usage: $0 <version> <patch>"
  exit 1
fi

if [ -z "$PATCH" ]; then
  echo "Usage: $0 <version> <patch>"
  exit 1
fi

# Replace the Keycloak image tag in Dockerfile
sed -i "s|quay.io/keycloak/keycloak:[^ ]*|quay.io/keycloak/keycloak:${VERSION}|" Dockerfile

# Replace the Keycloak image tag in README.md
sed -i "s|quay.io/keycloak/keycloak:[^ ]*|quay.io/keycloak/keycloak:${VERSION}|" README.md

# Replace the keycloak-egov-plugins image tag
sed -i "s|keycloak-egov-plugins:[^ ]*|keycloak-egov-plugins:${VERSION}.${PATCH}|" README.md

# Replace the <keycloak.version> property
sed -i "s|<keycloak.version>[^<]*</keycloak.version>|<keycloak.version>${VERSION}</keycloak.version>|" pom.xml

# Replace the <version> of the artifact (the first <version> after <artifactId>)
sed -i "0,/<version>[^<]*<\/version>/s|<version>[^<]*</version>|<version>${VERSION}.${PATCH}</version>|" pom.xml