#!/bin/bash

DESCRIPTION="Simple Login Android Client"

STANDALONE_DEST="../firebase-clients/java"
STANDALONE_STUB="firebase-simple-login"

cd $(dirname $0)

# Check for destination
if [[ ! -d $STANDALONE_DEST ]]; then
  echo "error: Destination directory for standalone artifact not found; 'firebase-client-js' needs to be a sibling of this repo."
  exit 1
fi

# Get version we're releasing
VERSION_CAND=$(grep version pom.xml |head -2|tail -1|awk -F '>' '{print $2}'|awk -F '<' '{print $1}'|awk -F '-' '{print $1}')

read -p "What version are we releasing? ($VERSION_CAND) " VER
if [[ -z $VER ]]; then
  VER=$VERSION_CAND
fi

# Check if we already have this as a standalone
STANDALONE_TARGET="${STANDALONE_DEST}/${STANDALONE_STUB}-${VER}.jar"
if [[ -e ${STANDALONE_TARGET} ]]; then
  echo "error: The target standalone already exists: ${STANDALONE_TARGET}"
  exit 1
fi

# Kick off standalone build
echo "Building standalone artifact"
mvn -DskipTests clean install

if [[ $? -ne 0 ]]; then
  echo "Error building artifact."
  exit 1
fi

STANDALONE_SRC="target/firebase-simple-login-${VER}-SNAPSHOT.jar"
if [[ ! -e $STANDALONE_SRC ]]; then
  echo "Source artifact not found. Check $STANDALONE_SRC"
  exit 1
fi

ls -l $STANDALONE_SRC

cp $STANDALONE_SRC $STANDALONE_TARGET
cp $STANDALONE_SRC ${STANDALONE_DEST}/${STANDALONE_STUB}-LATEST.jar

cd ${STANDALONE_DEST}/
git add .
git commit -am "[firebase-release] Updated Firebase $DESCRIPTION to $VER"
git push
if [[ $? -ne 0 ]]; then
  echo "Error pushing firebase-clients."
  exit 1
fi

cd -

echo "firebase-clients repo updated. Deploy to make changes live."

read -p "Next, make sure this repo is clean and up to date. We will be kicking off a deploy to Maven." DERP
mvn clean
mvn release:clean release:prepare release:perform

if [[ $? -ne 0 ]]; then
  echo "error: Error building and releasing to maven."
  exit 1
fi

echo "Manual steps:"
echo "  1) deploy firebase-clients"
echo "  2) release maven repo at http://oss.sonatype.org/"
echo "  3) Tweet: v${VER} of Android @Firebase SimpleLogin is available: https://cdn.firebase.com/java/firebase-simple-login-LATEST.jar Changelog: https://cdn.firebase.com/java/changelog-simple-login.txt"
echo ---
echo "Done! Woo!"
