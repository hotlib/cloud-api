#!/usr/bin/env bash
mvn -Dflyway.configFiles=flyway.properties flyway:migrate
