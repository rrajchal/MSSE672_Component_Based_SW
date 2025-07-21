#!/bin/bash

for name in a b c d; do 
  java -cp "$(pwd)/target/classes:$(pwd)/lib/log4j-api-2.20.0.jar:$(pwd)/lib/log4j-core-2.20.0.jar:$(pwd)/lib/jakarta.persistence-api-3.1.0.jar" \
    com.topcard.network.GameClient "$name" "$name" &
done
