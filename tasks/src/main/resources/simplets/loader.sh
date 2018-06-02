#!/bin/bash

echo "Creating materialized view...";

status=$(psql -U alex -h localhost -d postgres -p 5432 -c "SELECT dblink_connect('dbname=alex');")

if [[ ${status} == *"OK"* ]]; then
  echo "It's there!"
else
    echo "No dblink extension is installed. Installing ...";
    psql -U alex -h localhost -d postgres -p 5432 -c "CREATE EXTENSION dblink;"
fi

psql -U alex -h localhost -d postgres -p 5432 -c "CREATE MATERIALIZED VIEW duplicate_simplets_view AS
  SELECT * FROM dblink('dbname=alex', 'SELECT * FROM simplets') AS dupl (id BIGINT, value VARCHAR);"


while true; do
    echo "refresh mat view ...";
    psql -U alex -h localhost -d postgres -p 5432 -c "REFRESH MATERIALIZED VIEW duplicate_simplets_view;";
    echo "refreshed!";

    echo "Updated data:";

    psql -U alex -h localhost -d postgres -p 5432 -c "SELECT * FROM duplicate_simplets_view;"

    echo "sleeping...";
    sleep 10s;
done