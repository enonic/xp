sudo sysctl -w vm.max_map_count=262144
docker-compose -f es-compose.yml up --build --force-recreate
