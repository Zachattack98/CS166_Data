DROP TABLE WORK_EXPR;
DROP TABLE EDUCATIONAL_DETAILS;
DROP TABLE MESSAGE;
DROP TABLE CONNECTION_USR;
DROP TABLE USR;


CREATE TABLE USR(
	userId varchar(30) UNIQUE NOT NULL, 
	password varchar(30) NOT NULL,
	email text NOT NULL,
	name char(50),
	dateOfBirth date,
	PRIMARY KEY(userId)
);

CREATE TABLE WORK_EXPR(
	userId varchar(30) NOT NULL, 
	company char(50) NOT NULL, 
	role char(50) NOT NULL,
	location char(50),
	startDate date,
	endDate date,
	PRIMARY KEY(userId, company,role,startDate),
	FOREIGN KEY(userId) REFERENCES USR(userId)
);

CREATE TABLE EDUCATIONAL_DETAILS(
	userId varchar(30) NOT NULL, 
	instituitionName char(50) NOT NULL, 
	major char(50) NOT NULL,
	degree char(50) NOT NULL,
	startdate date,
	enddate date,
	PRIMARY KEY(userId, major,degree),
	FOREIGN KEY(userId) REFERENCES USR(userId)
);

CREATE TABLE MESSAGE(
	msgId integer UNIQUE NOT NULL, 
	senderId varchar(30) NOT NULL,
	receiverId varchar(30) NOT NULL,
	contents char(500) NOT NULL,
	sendTime timestamp,
	deleteStatus integer,
	status char(30) NOT NULL,
	PRIMARY KEY(msgId),
	FOREIGN KEY(senderId) REFERENCES USR(userId),
	FOREIGN KEY(receiverId) REFERENCES USR(userId)
);

CREATE TABLE CONNECTION_USR(
	userId varchar(30) NOT NULL, 
	connectionId char(30) NOT NULL, 
	status char(30) NOT NULL,
	PRIMARY KEY(userId, connectionId),
	FOREIGN KEY(userId) REFERENCES USR(userId)
);

COPY USR(userId, password, email, name, dateOfBirth)
FROM 'USR.csv'
DELIMITER ','
CSV HEADER;

COPY MESSAGE(msgId, senderId, receiverId, contents, sendTime, deleteStatus, status)
FROM 'message.csv'
DELIMITER ','
CSV HEADER;

COPY CONNECTION_USR(userId, connectionId, status)
FROM 'connection.csv'
DELIMITER ','
CSV HEADER;

COPY WORK_EXPR(userId, company, role, location, startDate, endDate)
FROM 'work_ex.csv'
DELIMITER ','
CSV HEADER;

COPY EDUCATIONAL_DETAILS(userId, instituitionName, major, degree, startdate, enddate)
FROM 'edu_det.csv'
DELIMITER ','
CSV HEADER;
