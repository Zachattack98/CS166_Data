SELECT COUNT(*)
FROM part_nyc N
WHERE N.on_hand > 70;


SELECT COUNT(N.on_hand), COUNT(S.on_hand), SUM(
       (SELECT COUNT(N1.on_hand)
       FROM part_nyc N1, color C1
       WHERE N1.color = C1.color_id AND C1.color_name = ‘Red’)
       +
       (SELECT COUNT(S1.on_hand)
       FROM part_sfo S1, color C1
       WHERE S1.color = C1.color_id AND C1.color_name = ‘Red’))
       AS total_red
FROM part_nyc N, part_sfo S, color C
WHERE N.color = C.color_id AND S.color = C.color_id AND C.color_name = ‘Red’;



SELECT s.supplier_name
FROM supplier s
WHERE ((SELECT SUM(N.on_hand)
    FROM part_nyc N
    WHERE N.supplier = s.supplier_id)
	     >
    (SELECT SUM(S.on_hand)
    FROM part_sfo S
    WHERE S.supplier = s.supplier_id))
    ORDER BY s.supplier_id;


SELECT s.supplier_name
FROM part_nyc N, supplier s
WHERE N.supplier = s.supplier_id AND N.part_number IN ((SELECT N1.part_number
    						    FROM part_nyc N1, supplier s1
    						    WHERE N1.supplier = s1.supplier_id)
	    						    EXCEPT
    						    (SELECT S.part_number
    						    FROM part_sfo S, supplier s1
    						    WHERE S.supplier = s1.supplier_id))
    						    ORDER BY s.supplier_id;


UPDATE part_nyc
SET on_hand = on_hand - 10
WHERE on_hand >= 10;

DELETE FROM part_nyc
WHERE on_hand > 30;
