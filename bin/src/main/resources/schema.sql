DROP TABLE IF EXISTS NOTES;

CREATE TABLE NOTES
(
  id VARCHAR
  AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR
  (250) NOT NULL,
  content VARCHAR
  (250) NOT NULL,

);