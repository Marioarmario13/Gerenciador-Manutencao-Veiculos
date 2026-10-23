# Gestão de Manutenção Preventiva de Veículos

Sistema em **Java (Swing) + MySQL (JDBC)** com arquitetura **MVC + DAO**, feito para a APS.

## Telas
| Tela | O que faz |
|---|---|
| **Login** | Autentica o usuário (tabela `usuario`). Padrão: `admin` / `admin123` |
| **Serviços** | Cadastro, consulta e preços dos serviços (listar, ordenar por 2 critérios, buscar, inserir, alterar, excluir) |
| **Mecânicos** | Cadastro e consulta de mecânicos (campo "Em atividade") |
| **Carros** | Dados do carro, mecânico atribuído, serviços e agenda (agendar, concluir, cancelar e remover) |

Nas telas de **Serviços**, **Mecânicos** e **Carros**, a busca é livre: filtre digitando qualquer informação dos registros (inclusive números e situação) e veja os resultados atualizados enquanto digita.

## Banco de dados
- `carro`, `servico` e `mecanico` são as entidades.
- `agenda` associa carros a serviços e guarda a data e o estado do serviço.
- As chaves estrangeiras protegem os relacionamentos. Ao excluir um carro, sua agenda é apagada junto; não é possível excluir um serviço agendado nem um mecânico atribuído a um carro.

## Como executar
1. Instale **JDK 11+**, **MySQL 8.0.16+** e o **MySQL Workbench**.
2. Execute `sql/schema.sql` no MySQL Workbench em um banco novo. O script cria as tabelas com dados de exemplo e apaga as tabelas existentes; não o execute em um banco com dados que queira preservar.
3. O MySQL Connector/J 26.7.0 está incluído na pasta `lib/`.
4. Copie `config.properties.example` para `config.properties` e ajuste `db.user` e `db.password` para as credenciais do MySQL. O arquivo local `config.properties` não é versionado.
5. Compile e execute:
   - Windows: `compilar.bat` e depois `executar.bat`
   - Linux/Mac: `./compilar.sh` e depois `./executar.sh`

Em uma IDE (NetBeans/IntelliJ/Eclipse), importe `src` como código-fonte, adicione os `.jar` da pasta `lib/` às bibliotecas e execute `app.Main` usando a pasta do projeto como diretório de trabalho.

## Estrutura
```
src/
  app/Main.java                 ponto de entrada (aplica o tema e abre o login)
  model/                        classes de dados: Carro, Servico, Mecanico, Agendamento, Usuario
  dao/                          acesso ao MySQL com JDBC (PreparedStatement)
    Conexao.java                abre a conexão e monta o ORDER BY das buscas
    CarroDAO, ServicoDAO, MecanicoDAO, AgendamentoDAO, UsuarioDAO
  controller/                   validações e regras de cada tela
    AuthController.java         login
    CarroController.java        carros + agenda de serviços do carro
    ServicoController.java      serviços
    MecanicoController.java     mecânicos
  view/                         telas Swing (Login, Principal, Carros, Servicos, Mecanicos)
    UI.java                     tema visual e funções para montar as telas
  util/
    Util.java                   validação e formatação (números, datas, moeda)
    AppException.java           erro com mensagem para o usuário (traduz erros do MySQL)
sql/schema.sql                  criação das tabelas + dados de exemplo
```

## Fluxo de uma ação (exemplo: salvar um carro)
1. **View** (`CarrosView`) lê os campos da tela e chama `CarroController.salvar(...)`.
2. **Controller** valida os dados (placa, ano, km...) e chama `CarroDAO.inserir` ou `atualizar`.
3. **DAO** executa o SQL no MySQL usando `Conexao.getConnection()`.
4. Se algo der errado, é lançada uma `AppException` com a mensagem, que a tela mostra num aviso.
