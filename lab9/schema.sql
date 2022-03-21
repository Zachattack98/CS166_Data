DROP TABLE IF EXISTS STORES;
CREATE TABLE STORES (
	storeid integer primary key,
	name varchar(100),
	state varchar(4),
	latitude real,
	longitude real);

DROP TABLE IF EXISTS CATALOG;
CREATE TABLE CATALOG (
        itemid integer primary key,
        itemname varchar(100));

DROP TABLE IF EXISTS SALES;
CREATE TABLE SALES (
        storeid integer,
        itemid integer,
        price real,
	time timestamp,
	primary key(storeid, itemid, time));

DROP TABLE IF EXISTS STATE_INFO;
CREATE TABLE STATE_INFO (
        state varchar(4) primary key,
        latitude real,
        longitude real);
