DO $$
DECLARE
    v_first_names TEXT[] := ARRAY['Juan', 'Carlos', 'José', 'Luis', 'Pedro', 'Manuel', 'Francisco', 'Ana', 'María', 'Carmen', 'Rosa', 'Luisa', 'Patricia', 'Yudelka', 'Altagracia', 'Luz', 'Miguel', 'Rafael', 'Jorge', 'Antonio', 'Ramón', 'Marcos', 'Yolanda', 'Xiomara', 'Kelvin'];
    v_last_names  TEXT[] := ARRAY['Pérez', 'Rodríguez', 'Gómez', 'Fernández', 'Martínez', 'Sánchez', 'Díaz', 'Reyes', 'Jiménez', 'Torres', 'Vargas', 'Castillo', 'Ramírez', 'Cruz', 'Morales', 'Peña', 'Rosario', 'Mejía', 'García', 'Mendoza', 'Pineda', 'Guzmán', 'Sosa', 'Abreu', 'Peralta'];
    v_biz_types   TEXT[] := ARRAY['Farmacia', 'Supermercado', 'Colmado', 'Repuesto', 'Inversiones', 'Distribuidora', 'Importadora', 'Servicios', 'Constructora', 'Comercial', 'Grupo', 'Centro Médico', 'Ferretería', 'Auto Import', 'Agropecuaria', 'Laboratorio', 'Transporte', 'Logística', 'Empresas', 'Consorcio'];
    v_biz_suf     TEXT[] := ARRAY['S.A.', 'S.R.L.', 'E.I.R.L.', 'S.A.S.', 'C. por A.'];

    v_cities      TEXT[] := ARRAY['Santo Domingo', 'Santiago de los Caballeros', 'La Vega', 'San Pedro de Macorís', 'La Romana', 'Punta Cana', 'Puerto Plata', 'San Cristóbal', 'Moca', 'Bonao', 'Higüey', 'Baní'];
    v_provinces   TEXT[] := ARRAY['Distrito Nacional', 'Santiago', 'La Vega', 'San Pedro de Macorís', 'La Romana', 'La Altagracia', 'Puerto Plata', 'San Cristóbal', 'Espaillat', 'Monseñor Nouel', 'La Altagracia', 'Peravia'];
    v_postcodes   TEXT[] := ARRAY['10101', '51000', '41000', '21000', '22000', '23000', '57000', '91000', '56000', '42000', '23001', '94000'];
    v_streets     TEXT[] := ARRAY['Av. Winston Churchill', 'Av. 27 de Febrero', 'Av. John F. Kennedy', 'Av. Las Américas', 'Calle El Conde', 'Av. Máximo Gómez', 'Av. Abraham Lincoln', 'Autopista Duarte Km ', 'Av. España', 'Av. San Martín', 'Calle Sol', 'Av. Luperón'];

    v_cust_id UUID;
    v_loc_id1 UUID;
    v_loc_id2 UUID;

    v_first_name TEXT;
    v_last_name  TEXT;
    v_owner      TEXT;
    v_biz_name   TEXT;
    v_email      TEXT;
    v_phone      TEXT;
    v_rnc        TEXT;

    v_city_idx INT;
    i INT;
BEGIN
    FOR i IN 1..105 LOOP
        v_cust_id := gen_random_uuid();
        v_loc_id1 := gen_random_uuid();
        v_loc_id2 := gen_random_uuid();

        v_first_name := v_first_names[1 + ((i * 3) % array_length(v_first_names, 1))];
        v_last_name  := v_last_names[1 + ((i * 7) % array_length(v_last_names, 1))];
        v_owner      := v_first_name || ' ' || v_last_name;

        v_biz_name   := v_biz_types[1 + (i % array_length(v_biz_types, 1))] || ' ' ||
                        v_last_names[1 + ((i * 2) % array_length(v_last_names, 1))] || ' ' ||
                        v_biz_suf[1 + (i % array_length(v_biz_suf, 1))];

        v_email      := 'contacto' || i || '@' || LOWER(REPLACE(v_last_names[1 + ((i * 2) % array_length(v_last_names, 1))], 'ñ', 'n')) || i || '.com.do';
        v_phone      := '809-' || LPAD((500 + i)::text, 3, '0') || '-' || LPAD((1000 + i * 13)::text, 4, '0');
        v_rnc        := '101-' || LPAD((10000 + i * 77)::text, 5, '0') || '-' || (i % 9);

        INSERT INTO customers (id, commercial_name, owner_name, email, phone, rnc, created_at, updated_at)
        VALUES (v_cust_id, v_biz_name, v_owner, v_email, v_phone, v_rnc, NOW(), NOW())
        ON CONFLICT (email) DO NOTHING;

        v_city_idx := 1 + (i % array_length(v_cities, 1));

        INSERT INTO locations (id, street_address, city, state_province, country, postal_code, created_at)
        VALUES (
            v_loc_id1,
            v_streets[1 + (i % array_length(v_streets, 1))] || ' #' || (10 + i),
            v_cities[v_city_idx],
            v_provinces[v_city_idx],
            'Dominican Republic',
            v_postcodes[v_city_idx],
            NOW()
        )
        ON CONFLICT (street_address, city, country, postal_code) DO NOTHING;

        INSERT INTO customer_locations (customer_id, location_id, is_primary, assigned_at)
        VALUES (v_cust_id, v_loc_id1, TRUE, NOW())
        ON CONFLICT (customer_id, location_id) DO NOTHING;

        IF i % 2 = 0 THEN
            v_city_idx := 1 + ((i + 3) % array_length(v_cities, 1));

            INSERT INTO locations (id, street_address, city, state_province, country, postal_code, created_at)
            VALUES (
                v_loc_id2,
                v_streets[1 + ((i + 4) % array_length(v_streets, 1))] || ' Plaza ' || (100 + i),
                v_cities[v_city_idx],
                v_provinces[v_city_idx],
                'Dominican Republic',
                v_postcodes[v_city_idx],
                NOW()
            )
            ON CONFLICT (street_address, city, country, postal_code) DO NOTHING;

            INSERT INTO customer_locations (customer_id, location_id, is_primary, assigned_at)
            VALUES (v_cust_id, v_loc_id2, FALSE, NOW())
            ON CONFLICT (customer_id, location_id) DO NOTHING;
        END IF;

    END LOOP;
END $$;
