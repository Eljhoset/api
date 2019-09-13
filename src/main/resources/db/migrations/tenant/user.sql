CREATE TABLE IF NOT EXISTS users (
    username varchar(100) primary key,
    password text not null
);
CREATE TABLE IF NOT EXISTS users_roles (
    username varchar(100) references  users(username),
    roles varchar(100),
    primary key(username,roles)
);