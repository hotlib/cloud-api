CREATE TABLE devmandpoc.devicedata
(
    id         serial PRIMARY KEY,
    devicename jsonb,
    devicedata jsonb
);