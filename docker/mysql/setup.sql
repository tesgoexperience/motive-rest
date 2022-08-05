DROP DATABASE IF EXISTS motivedb;
CREATE DATABASE motivedb;
GRANT ALL ON motivedb.* TO 'motiveuser'@'%';

ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';

FLUSH PRIVILEGES;

