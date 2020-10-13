create sequence public.id_generator_seq increment 1 start 1 cache 1;

create table users
(
    id       bigint primary key default nextval('public.id_generator_seq'),
    username varchar(100) unique not null,
    password varchar(500)       not null
);

create table route
(
    id            bigint primary key default nextval('public.id_generator_seq'),
    name          varchar(100) not null,
    creation_time timestamp    not null,
    distance      int          not null,
    user_id       bigint references users (id)
);

create table coordinates
(
    id       bigint primary key default nextval('public.id_generator_seq'),
    x        bigint not null,
    y        int,
    route_id bigint references route (id)
        on delete cascade
);

create table location
(
    id       bigint primary key default nextval('public.id_generator_seq'),
    x        double precision not null,
    y        double precision not null,
    z        int              not null,
    isFrom   boolean          not null,
    route_id bigint references route (id)
        on delete cascade
);