sudo apt-get -y update; 
sudo apt-get -y install mariadb-server; 
sudo service mariadb start; 
sudo mysql --password=root < docker/mysql/setup.sql;

 # install openssl
apt list -a openssl;
openssl version -al;
mkdir ./src/main/resources/certs;

# generate openssl private and primary key
openssl genpkey -out ./src/main/resources/certs/private-key.pem -algorithm RSA -pkeyopt rsa_keygen_bits:2048
openssl rsa -in ./src/main/resources/certs/private-key.pem -pubout -out ./src/main/resources/certs/public-key.pem;

head -n 1 ./src/main/resources/certs/public-key.pem;
head -n 1 ./src/main/resources/certs/private-key.pem;

