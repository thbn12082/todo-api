create table if not exists todos (
    id bigserial primary key,
    title varchar(100) not null,
    completed boolean not null default false,
    description varchar(200),
    priority int not null
);