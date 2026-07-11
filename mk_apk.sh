#!/bin/bash

# Setup java environment for Android Studio JBR
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
export PATH="$JAVA_HOME/bin:$PATH"

./gradlew assembleRelease \
	-PRELEASE_STORE_FILE=../android_keystore		\
	-PRELEASE_STORE_PASSWORD="#Leteci#Cirkus#"		\
	-PRELEASE_KEY_ALIAS="music_player"				\
	-PRELEASE_KEY_PASSWORD="#Leteci#Cirkus#"
