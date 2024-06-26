create table if not exists statement
(
    made_at          date                                           not null,
    type             text                                           not null,
    sequence_of_day  integer CHECK (statement.sequence_of_day >= 0) not null,
    primary key (made_at, type, sequence_of_day),
    version          integer,
    state            text                                           not null,
    author_id        text                                           not null,
    created_by       varchar(50),
    created_at       timestamp                                      not null,
    last_modified_by varchar(50),
    last_modified_at timestamp
);

create table if not exists reaction
(
    id   serial primary key,
    type text not null,
    text text not null
);
