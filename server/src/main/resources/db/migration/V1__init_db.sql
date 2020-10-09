create table users
(
    id       bigserial primary key,
    username varchar(100) not null,
    password varchar(1000) not null
);

create table route
(
    id            bigserial primary key,
    name          varchar(100) not null,
    creation_time timestamp    not null,
    distance      int          not null,
    user_id       bigint references users (id)
);

create table coordinates
(
    id       bigserial primary key,
    x        bigint not null,
    y        int,
    route_id bigint references route (id)
);

create table location
(
    id       bigserial primary key,
    x        double precision not null,
    y        double precision not null,
    z        int              not null,
    isFrom   boolean          not null,
    route_id bigint references route (id)
);