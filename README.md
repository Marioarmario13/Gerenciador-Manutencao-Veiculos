# Gestão de Manutenção Preventiva de Veículos

Sistema em **Java (Swing) + PostgreSQL (JDBC)** com arquitetura **MVC + DAO**, feito para a APS (CRUD completo com interface gráfica).

## Telas
| Tela | O que faz |
|---|---|
| **Login** | Autentica o usuário (tabela `usuario`). Padrão: `admin` / `admin123` |
| **Serviços** | Cadastro, consulta e preços dos serviços (listar, ordenar por 2 critérios, buscar, inserir, alterar, excluir) |
| **Mecânicos** | Mecânicos que trabalham na oficina (campo "Em atividade") |
| **Carros** | Dados do carro, problema, mecânico atribuído, serviços e agenda (agendar, concluir, cancelar, remover; total previsto) |

## Banco de dados (3 entidades + 1 tabela de relação)
- `servico`, `mecanico`, `carro` (FK → `mecanico`)
- `agenda` = tabela associativa **carro x serviço** (FKs para `carro` e `servico`)
- `usuario` (login)

Integridade: não é possível excluir um **serviço** que está em alguma agenda, nem um **mecânico** atribuído a um carro (o sistema avisa). Ao excluir um **carro**, a agenda dele é apagada junto (`ON DELETE CASCADE`).

## Como rodar
1. Instale **JDK 11+**, **PostgreSQL 18** e **pgAdmin 4**.
2. No pgAdmin, clique com o botão direito em **Servers > Register > Server**. Use um nome como `PostgreSQL 18 - Local`; na aba **Connection**, informe host `localhost`, porta `5432`, banco de manutenção `postgres` e usuário `postgres`, além da senha definida na instalação.
3. Crie o banco `manutencao_veiculos` (clique com o botão direito em **Databases > Create > Database**) e abra seu **Query Tool**.
4. Execute `sql/schema.sql` uma vez no banco vazio. O script recria as tabelas; não o execute novamente se quiser preservar os dados.
5. O driver PostgreSQL JDBC 42.7.14 já está incluído em `lib/`.
6. Copie `config.properties.example` para `config.properties` e ajuste `db.user` e `db.password` para as credenciais do servidor PostgreSQL. O arquivo local `config.properties` não é versionado.
7. Compile e execute:
   - Windows: `compilar.bat` e depois `executar.bat`
   - Linux/Mac: `./compilar.sh` e depois `./executar.sh`

Em IDE (NetBeans/IntelliJ/Eclipse): importe a pasta `src` como código-fonte, adicione o `.jar` da `lib/` às bibliotecas, rode `app.Main` e execute com a pasta do projeto como diretório de trabalho (para ler o `config.properties`).

## Estrutura
```
src/
  app/Main.java           ponto de entrada
  model/                  classes de dados (Servico, Mecanico, Carro, Agendamento, Usuario)
  dao/                    acesso ao PostgreSQL (JDBC, PreparedStatement)
  controller/             validações e regras, traduz erros do banco
  view/                   telas Swing
  util/                   validação e formatação (datas, moeda)
sql/schema.sql            criação das tabelas + dados de exemplo
```
