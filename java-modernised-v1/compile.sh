#!/bin/bash
JAVA_HOME="${HOME}/.asdf/installs/java/temurin-25.0.1+8.0.LTS"
JAVAC="$JAVA_HOME/bin/javac"

CP="lib/angus-mail-2.0.3.jar"
CP="$CP:lib/angus-activation-2.0.2.jar"
CP="$CP:lib/jakarta.mail-api-2.1.3.jar"
CP="$CP:lib/jakarta.activation-api-2.1.3.jar"
CP="$CP:lib/swing-layout-1.0.4.jar"
CP="$CP:lib/commons-codec-1.17.1.jar"
CP="$CP:lib/commons-lang3-3.17.0.jar"
CP="$CP:lib/jazzy-core.jar"
CP="$CP:lib/jazzy-swing.jar"

mkdir -p classes

"$JAVAC" \
  -cp "$CP" \
  -d classes \
  --add-exports java.desktop/sun.awt=ALL-UNNAMED \
  etmaHandler/*.java
