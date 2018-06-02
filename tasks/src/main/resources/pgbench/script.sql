SELECT *
FROM poems
WHERE to_tsvector('english', content) @@ to_tsquery('english', 'jumped | sleep');