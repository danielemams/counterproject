#!/bin/bash

set -e

WILDFLY_HOME=/opt/jboss/wildfly
CONFIG_MARKER=${WILDFLY_HOME}/.configured

# Configure WildFly on first run
if [ ! -f "$CONFIG_MARKER" ]; then
    echo "First run detected - configuring WildFly..."
    ${WILDFLY_HOME}/bin/jboss-cli.sh --file=${WILDFLY_HOME}/configure-wildfly.cli
    touch "$CONFIG_MARKER"
    echo "WildFly configured successfully"
fi

# Start WildFly
echo "Starting WildFly..."
exec ${WILDFLY_HOME}/bin/standalone.sh -b 0.0.0.0
