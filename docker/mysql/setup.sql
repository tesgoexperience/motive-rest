DROP DATABASE IF EXISTS motivedb;
CREATE DATABASE motivedb;
CREATE USER 'motiveuser'@'%' IDENTIFIED BY 'motivepassword';
FLUSH PRIVILEGES;
GRANT ALL ON motivedb.* TO 'motiveuser'@'%';
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;