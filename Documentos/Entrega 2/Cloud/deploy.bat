@echo off
echo 🚀 Iniciando Deploy do Backend Codigo_PI...

REM Build e execução dos containers
docker-compose up --build -d

echo.
echo ✅ Ambiente subiu com sucesso!
echo 📍 API: http://localhost:8080
echo 📍 Banco Postgres: localhost:5432
echo.
echo Use "docker-compose ps" para ver o status.
pause
