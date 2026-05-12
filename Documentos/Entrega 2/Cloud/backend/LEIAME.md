# Documentação de Containerização - Projeto Codigo_PI

Este projeto contém a infraestrutura necessária para rodar o backend da aplicação de forma containerizada, seguindo os princípios de Cloud Native.

##  Como Executar

### Pré-requisitos
* Docker e Docker Compose instalados.

### Execução Automatizada
Para subir todo o ambiente (API + Banco de Dados), execute o script de deploy:
```bash
chmod +x deploy.sh
./deploy.sh
```

### Acesso à API
A API estará disponível em: `http://localhost:8080`
Endpoints principais:
* `GET /api/usuarios`
* `GET /api/treinos/usuario/{id}`
* `POST /api/historico`

---

## 📄 Relatório Técnico

### 1. Vantagens da Containerização
* **Portabilidade:** O sistema funciona exatamente da mesma forma em qualquer computador (Windows, Linux, Cloud), eliminando o problema de "na minha máquina funciona".
* **Isolamento:** As dependências do backend (Java, Postgres) não conflitam com outros softwares instalados no sistema hospedeiro.
* **Escalabilidade:** É possível subir múltiplas instâncias da API rapidamente para aguentar mais tráfego.

### 2. Diferença entre Ambiente Tradicional e Cloud Native
* **Ambiente Tradicional:** Geralmente envolve a instalação manual de servidores (Apache, Postgres) diretamente no Sistema Operacional. A atualização é difícil e o ambiente é rígido.
* **Ambiente Cloud Native (Docker):** A aplicação é dividida em micro-serviços isolados. Tudo é definido como código (Infrastructure as Code), facilitando o deploy contínuo (CI/CD) e a recuperação automática de falhas em provedores de nuvem como AWS ou Google Cloud.

### 3. Orquestração
O `docker-compose.yml` gerencia a comunicação entre a API e o banco, garante que o banco suba primeiro e mantém os dados salvos no volume `postgres_data`, mesmo que o container seja deletado.
