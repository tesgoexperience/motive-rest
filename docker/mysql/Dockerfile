FROM mysql:latest
ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DB motivedb
ENV MYSQL_USER motiveuser
ENV MYSQL_PASSWORD motivepassword
ADD setup.sql ./setup.sql
EXPOSE 3306