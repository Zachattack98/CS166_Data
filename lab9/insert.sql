COPY stores(storeid, name, state, latitude, longitude)
FROM 'stores.csv'
DELIMITER ','
CSV HEADER;

COPY sales(storeid, itemid, price, time)
FROM 'sales.csv'
DELIMITER ','
CSV HEADER;

COPY catalog(itemid, itemname)
FROM 'catalog.csv'
DELIMITER ','
CSV HEADER;
