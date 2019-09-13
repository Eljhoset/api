--changeset dbms:h2
CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS public.tenant(
    name varchar(100) primary key
);
--changeset dbms:h2
MERGE INTO tenant KEY(name) VALUES ('br');