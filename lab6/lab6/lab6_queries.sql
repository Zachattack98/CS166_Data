SELECT S.sname, COUNT(P.pname)
FROM Suppliers S, Parts P, Catalog C
WHERE S.sid = C.sid AND P.pid = C.pid
GROUP BY S.sname;

SELECT S.sname, COUNT(P.pname) as Count_Parts
FROM Suppliers S, Parts P, Catalog C
WHERE S.sid = C.sid AND P.pid = C.pid
GROUP BY S.sname
HAVING COUNT(P.pname) > 2;

SELECT S.sname, COUNT(P.pname)
FROM Suppliers S, Parts P, Catalog C
WHERE S.sid = C.sid AND P.pid = C.pid AND S.sid IN (
SELECT S1.sid
FROM Suppliers S1, Parts P1, Catalog C1
WHERE S1.sid = C1.sid AND P1.pid = C1.pid AND P1.color = 'Green')
GROUP BY S.sname;

SELECT S.sname, MAX(C.cost)
FROM Suppliers S, Parts P, Catalog C
WHERE S.sid = C.sid AND P.pid = C.pid AND (P.color = 'Green' OR P.color = 'Red')
GROUP BY S.sname;
