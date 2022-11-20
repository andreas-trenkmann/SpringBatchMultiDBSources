# create databases
CREATE DATABASE IF NOT EXISTS `target1`;
CREATE DATABASE IF NOT EXISTS `target2`;
CREATE DATABASE IF NOT EXISTS `source1`;

# create root user and grant rights
CREATE USER 'export'@'%' IDENTIFIED BY 'export';
GRANT ALL PRIVILEGES ON *.* TO 'export'@'%';