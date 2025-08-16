#!/bin/bash

BASE_DIR="$(pwd)"

CP="$BASE_DIR/target/classes"
CP+=":$BASE_DIR/lib/log4j-api-2.20.0.jar"
CP+=":$BASE_DIR/lib/log4j-core-2.20.0.jar"
CP+=":$BASE_DIR/lib/jakarta.persistence-api-3.1.0.jar"
CP+=":$BASE_DIR/lib/hibernate-core-6.4.4.Final.jar"
CP+=":$BASE_DIR/lib/jboss-logging-3.5.0.Final.jar"
CP+=":$BASE_DIR/lib/jakarta.transaction-api-2.0.1.jar"
CP+=":$BASE_DIR/lib/jakarta.xml.bind-api-4.0.0.jar"
CP+=":$BASE_DIR/lib/jaxb-runtime-4.0.3.jar"
CP+=":$BASE_DIR/lib/classmate-1.5.1.jar"
CP+=":$BASE_DIR/lib/hibernate-commons-annotations-6.0.6.Final.jar"
CP+=":$BASE_DIR/lib/mysql-connector-j-8.0.33.jar"
CP+=":$BASE_DIR/lib/byte-buddy-1.12.23.jar"
CP+=":$BASE_DIR/lib/antlr4-runtime-4.9.3.jar"

echo "Starting TopCard Server..."
java -cp "$CP" com.topcard.network.game.GameServer
