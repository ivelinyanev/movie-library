create table movies
(
    rating   double       null,
    year     int          not null,
    movie_id bigint auto_increment
        primary key,
    director varchar(255) not null,
    title    varchar(255) not null,
    constraint UKgovm2dombrdnujupo3o7hvtep
        unique (title)
);

create table roles
(
    role_id bigint auto_increment
        primary key,
    name    enum ('ROLE_ADMIN', 'ROLE_USER') null
);

create table users
(
    user_id  bigint auto_increment
        primary key,
    password varchar(255) not null,
    username varchar(255) not null,
    constraint UKr43af9ap4edm43mmtq01oddj6
        unique (username)
);

create table users_roles
(
    role_id bigint not null,
    user_id bigint not null,
    primary key (user_id, role_id),
    constraint FK2o0jvgh89lemvvo17cbqvdxaa
        foreign key (user_id) references users (user_id),
    constraint FKj6m8fwv7oqv74fcehir1a9ffy
        foreign key (role_id) references roles (role_id)
);

