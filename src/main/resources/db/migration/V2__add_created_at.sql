alter table todos
add column if not exists created_at timestamptz default now();