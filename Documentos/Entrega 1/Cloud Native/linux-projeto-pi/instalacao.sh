#!/bin/bash

echo "Atualizando sistema..."
sudo apt update && sudo apt upgrade -y

echo "Instalando dependências..."
sudo apt install -y git curl vim

echo "Instalação concluída!"
