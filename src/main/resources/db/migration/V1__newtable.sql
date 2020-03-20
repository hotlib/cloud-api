CREATE SCHEMA IF NOT EXISTS devmandpoc;

CREATE TABLE devmandpoc.devicedata
(
    id         serial PRIMARY KEY,
    devicename VARCHAR(2048),
    devicedata jsonb
);

CREATE TABLE devmandpoc.organizations
(
    organization_id serial PRIMARY KEY,
    api_key VARCHAR(512)
);

INSERT INTO devmandpoc.organizations (api_key) VALUES ('testingapikey');