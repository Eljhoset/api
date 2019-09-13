CREATE TABLE IF NOT EXISTS  model (
    id bigint auto_increment primary key,
    name varchar(255) not null,
    created_by varchar(100),
    created_at timestamp default now(),
    updated_by varchar(100),
    updated_at timestamp,
    deleted_by varchar(100),
    deleted_at timestamp,
    status varchar(100) default 'ACTIVE'
);

insert into model(name) values ('model 1');
insert into model(name) values ('model 2');
insert into model(name,status) values ('model 3','DELETE');