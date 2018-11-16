create table subscriptions(
  id bigserial primary key,
  email varchar(120) not null,
  repository_name varchar(120) not null,
  repository_owner varchar(120) not null,
  check_interval_in_seconds bigint not null,
  last_check_ts timestamp not null
)