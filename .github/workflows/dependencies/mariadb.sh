# Why not use services? Services are not implemented in act making it difficult to develop/debug a service. Follow this thread for availability https://github.com/nektos/act/issues/173
docker pull mariadb:latest

docker build -t mysqldb ./docker/mysql/
docker run --name motivedb -p3306:3306 -d mysqldb
# docker run --name mariadatabase -e MYSQL_ROOT_PASSWORD=root MYSQL_DB=motivedb MYSQL_USER=motiveuser MYSQL_PASSWORD=motivepassword -p 3306:3306  -d docker.io/library/mariadb:latest