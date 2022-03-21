EXPLAIN ANALYZE select *
FROM STORES JOIN (SALES JOIN CATALOG ON sales.itemid = catalog.itemid) ON stores.storeid = sales.storeid;

EXPLAIN ANALYZE select *
FROM STORES JOIN (SALES JOIN CATALOG ON sales.itemid = catalog.itemid) ON stores.storeid = sales.storeid
WHERE stores.state='CA';
