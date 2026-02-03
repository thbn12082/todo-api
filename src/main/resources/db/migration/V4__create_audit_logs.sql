create table if not exists audit_logs (
    id bigserial primary key,
    action varchar(255) not null,
    reference_id bigint,
    created_at timestamptz not null default now()
);



