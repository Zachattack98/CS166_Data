CREATE INDEX part_nyc_pnum_index
ON part_nyc(part_number);

CREATE INDEX part_nyc_supplier_index
ON part_nyc(supplier);

CREATE INDEX part_nyc_color_index
ON part_nyc(color);

CREATE INDEX part_nyc_oh_index
ON part_nyc(on_hand);

CREATE INDEX part_sfo_pnum_index
ON part_sfo(part_number);

CREATE INDEX part_sfo_supplier_index
ON part_sfo(supplier);

CREATE INDEX part_sfo_color_index
ON part_sfo(color);

CREATE INDEX part_sfo_oh_index
ON part_sfo(on_hand);

CREATE INDEX supplier_sid_index
ON supplier(supplier_id);

CREATE INDEX supplier_sname_index
ON supplier(supplier_name);

CREATE INDEX color_cid_index
ON color(color_id);

CREATE INDEX color_cname_index
ON color(color_name);
