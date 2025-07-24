#!/bin/bash

for i in a b c d; do
  java -jar ./target/TopCard-1.0-SNAPSHOT-jar-with-dependencies.jar "$i" &
done

wait
