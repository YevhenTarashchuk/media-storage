FROM ubuntu:18.04

RUN apt-get update && apt-get install -y postgresql-10

ARG DATABASE_NAME
ARG DB_USERNAME
ARG DB_PASSWORD

USER postgres
RUN /etc/init.d/postgresql start && /usr/bin/psql --command "DROP DATABASE IF EXISTS $DATABASE_NAME;"

RUN    /etc/init.d/postgresql start &&\
    psql --command "CREATE USER $DB_USERNAME WITH SUPERUSER PASSWORD '$DB_PASSWORD';" &&\
    createdb -O $DB_USERNAME $DATABASE_NAME

RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/10/main/pg_hba.conf

RUN echo "listen_addresses='*'" >> /etc/postgresql/10/main/postgresql.conf

EXPOSE 5432

VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

CMD ["/usr/lib/postgresql/10/bin/postgres", "-D", "/var/lib/postgresql/10/main", "-c", "config_file=/etc/postgresql/10/main/postgresql.conf"]
