# 🏢 Controle de Visitantes

Sistema web de controle de entrada e saída de visitantes desenvolvido como **Projeto Integrador** do curso **Jovem Programador**.

---

## 📋 Sobre o Projeto

Sistema completo para gerenciamento de visitas em ambientes corporativos, permitindo registrar entradas e saídas, agendar visitas com antecedência e controlar o acesso por departamento. Desenvolvido com Java + Spring Boot e containerizado com Docker.

---

## 🚀 Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 17 + Spring Boot 3.3 |
| Segurança | Spring Security + BCrypt |
| Banco de Dados | MySQL 8.x via JdbcTemplate |
| Frontend | Thymeleaf + HTML5 + CSS3 + JavaScript |
| Build | Maven |
| Containerização | Docker + Docker Compose |
| Deploy | Linux + SSH + rsync |

---

## ✨ Funcionalidades

- **Autenticação** com login/senha e bloqueio por CAPTCHA após 3 tentativas falhas
- **Cadastro de Visitantes** com máscara automática de CPF e telefone
- **Cadastro de Departamentos** e **Usuários**
- **Registro de Entrada** com seleção de múltiplos visitantes por visita
- **Registro de Saída** com data/hora automática
- **Agendamento de Visitas** com status (Agendado / Cancelado / Concluído)
- **Check-in** de agendamentos do dia diretamente na tela de visitas
- **Bloqueio de visita duplicada** — visitante em visita ativa fica desabilitado para nova seleção
- **Tema claro/escuro** com persistência no navegador

---

## 🗄️ Modelo de Dados

```
usuarios          — login, senha (BCrypt), nome, perfil
departamentos     — nome
visitantes        — nome, CPF, telefone, empresa
visitas           — data_entrada, data_saida, departamento_id, usuario_id, motivo
visitantes_visitas — (N:N) visitante ↔ visita
agendamentos      — data_agendamento, status, departamento_id, usuario_id, motivo
agendamentos_visitantes — (N:N) visitante ↔ agendamento
```

---

## ⚙️ Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados
- Porta `4000` disponível no host

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/controle-visitantes.git
cd controle-visitantes
```

### 2. Suba os containers

```bash
docker compose up -d
```

O banco é inicializado automaticamente via `init.sql`.  
Aguarde o healthcheck do MySQL (~30s) antes de acessar a aplicação.

### 3. Acesse

```
http://localhost:4000/login
```

| Campo | Valor |
|-------|-------|
| Usuário | `admin` |
| Senha | `admin123` |

---

## 🔧 Execução local (sem Docker)

### Pré-requisitos

- Java 17+
- Maven 3.9+
- MySQL 8 rodando localmente

### Configurar banco

```sql
CREATE DATABASE controle_visitantes;
-- Execute o script init.sql para criar tabelas e usuário inicial
```

### Configurar `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/controle_visitantes
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

### Executar

```bash
mvn spring-boot:run
```

---

## 🐳 Estrutura Docker

```
controle-visitantes/
├── Dockerfile              # Build multi-stage (Maven + JRE Alpine)
├── docker-compose.yml      # App (porta 4000) + MySQL (porta 3307)
├── init.sql                # Criação de tabelas e dados iniciais
├── deploy_full.sh          # Deploy local → /container/projIntegrador
└── deploy_remote.sh        # Deploy remoto via SSH (rsync + docker compose)
```

### Variáveis de ambiente (docker-compose)

| Variável | Padrão |
|----------|--------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://db:3306/controle_visitantes` |
| `SPRING_DATASOURCE_USERNAME` | `ajava` |
| `SPRING_DATASOURCE_PASSWORD` | `SenhaForte123@` |

---

## 📁 Estrutura do Projeto

```
src/main/
├── java/br/com/controlevisitantes/
│   ├── SecurityConfig.java          # Spring Security + filtros
│   ├── CaptchaFilter.java           # Filtro de CAPTCHA (OncePerRequestFilter)
│   ├── LoginAttemptService.java     # Contagem de tentativas (HttpSession)
│   ├── LoginController.java         # Tela de login + geração de CAPTCHA
│   ├── MenuController.java
│   ├── VisitaController.java        # Entrada, saída, check-in
│   ├── VisitaService.java
│   ├── VisitanteController.java
│   ├── VisitanteService.java
│   ├── DepartamentoController.java
│   ├── DepartamentoService.java
│   ├── AgendamentoController.java
│   └── AgendamentoService.java
└── resources/
    ├── application.properties
    └── templates/
        ├── login.html
        ├── menu.html
        ├── visita.html
        ├── visitante.html
        ├── departamento.html
        └── agendamento.html
```

---

## 🔒 Segurança

- Senhas armazenadas com **BCrypt**
- Autenticação via **Spring Security**
- **CAPTCHA matemático** ativado após 3 tentativas de login falhas
- Validação de **visita duplicada** no back-end com rollback automático
- Rotas protegidas — apenas `/login` é pública

---

## 📄 Documentação

| Documento | Descrição |
|-----------|-----------|
| `Versao1.docx` | Documento de versão v1.0.0 — funcionalidades iniciais |
| `Correcao_v1_1.docx` | Documento de versão v1.1.0 — correção de visita duplicada |
| `GMUD_ControleVisitantes.docx` | Gestão de Mudanças — plano de deploy e rollback |
| `PoliticaBackup_ControleVisitantes.docx` | Política de Backup e Resiliência de Dados |

---

## 👨‍💻 Autor

**André Marcelo**  
Curso Jovem Programador — Projeto Integrador 2026
