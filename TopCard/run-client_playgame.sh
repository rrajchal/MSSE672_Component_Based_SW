#!/bin/bash

# For 4 players: a, b, c and d
for i in a b c d; do
  java -jar ./target/TopCard-1.0-SNAPSHOT-jar-with-dependencies.jar "$i" &
done

wait
