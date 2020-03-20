#!/usr/bin/env bash

# first create/update the database schema (if needed)
java -cp ".:cloudapi-1.0-SNAPSHOT-jar-with-dependencies.jar" io.frinx.App -c

# launch the grpc server
java -cp ".:cloudapi-1.0-SNAPSHOT-jar-with-dependencies.jar" io.frinx.App