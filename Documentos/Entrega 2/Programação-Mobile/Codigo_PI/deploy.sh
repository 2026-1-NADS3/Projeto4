#!/bin/bash

echo "🚀 Iniciando o processo de deploy automatizado..."

# 1. Parar containers antigos e remover volumes (limpeza opcional)
# echo "🧹 Limpando ambiente anterior..."
# docker-compose down -v

# 2. Construir as imagens e subir os containers em segundo plano
echo "📦 Construindo imagens e subindo containers..."
docker-compose up --build -d

# 3. Verificar status
echo "🔍 Verificando status dos serviços..."
docker-compose ps

echo "✅ Deploy concluído com sucesso!"
echo "📍 API rodando em: http://localhost:8080"
