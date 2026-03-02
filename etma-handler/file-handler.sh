#!/bin/bash
# OU eTMA File Handler - Updated 2026-01-05
# Fully modernized: Java 25 + Jakarta EE + updated dependencies

handler="File Handler"
string=$(xprop -name "$handler" 2> /dev/null | grep -P '_NET_WM_NAME')
BASEDIR="$(dirname "$(readlink -f "$0")")"

# Use asdf-managed Java (Temurin 25)
JAVA_HOME="${HOME}/.asdf/installs/java/temurin-25.0.1+8.0.LTS"
JAVA_BIN="${JAVA_HOME}/bin/java"

# Fallback to bundled if asdf Java not found
if [[ ! -x "$JAVA_BIN" ]]; then
    JAVA_BIN="${BASEDIR}/dist/jre/bin/java"
fi

# Modern classpath with all dependencies
LIBDIR="${BASEDIR}/modernized/lib"
CP="${BASEDIR}/modernized/etmaHandlerJ-modernized.jar"
CP="${CP}:${LIBDIR}/angus-mail-2.0.3.jar"
CP="${CP}:${LIBDIR}/angus-activation-2.0.2.jar"
CP="${CP}:${LIBDIR}/jakarta.mail-api-2.1.3.jar"
CP="${CP}:${LIBDIR}/jakarta.activation-api-2.1.3.jar"
CP="${CP}:${LIBDIR}/swing-layout-1.0.4.jar"
CP="${CP}:${LIBDIR}/commons-codec-1.17.1.jar"
CP="${CP}:${LIBDIR}/commons-lang3-3.17.0.jar"
CP="${CP}:${LIBDIR}/jazzy-core.jar"
CP="${CP}:${LIBDIR}/jazzy-swing.jar"

if [[ $string =~ $handler ]]
then
   # File Handler is running - bring to front
   "${BASEDIR}/wmctrl" -a "$handler"
else
   # Launch File Handler with modern Java
   cd "${BASEDIR}"
   exec "$JAVA_BIN" \
       --add-opens=java.base/java.lang=ALL-UNNAMED \
       --add-opens=java.base/java.util=ALL-UNNAMED \
       --add-opens=java.base/java.io=ALL-UNNAMED \
       --add-opens=java.desktop/java.awt=ALL-UNNAMED \
       --add-opens=java.desktop/javax.swing=ALL-UNNAMED \
       --add-opens=java.desktop/javax.swing.plaf.basic=ALL-UNNAMED \
       --add-opens=java.desktop/javax.swing.text=ALL-UNNAMED \
       --add-opens=java.desktop/javax.swing.table=ALL-UNNAMED \
       --add-opens=java.desktop/sun.awt=ALL-UNNAMED \
       --add-opens=java.prefs/java.util.prefs=ALL-UNNAMED \
       -cp "$CP" \
       etmaHandler.EtmaHandlerJ "$@"
fi
