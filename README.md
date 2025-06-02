# Avaliação técnica
## Objetivo

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias,
por votação. Imagine que você deve criar uma solução para dispositivos móveis para gerenciar e
participar dessas sessões de votação.
Essa solução deve ser executada na nuvem e promover as seguintes funcionalidades através de
uma API REST:

● Cadastrar uma nova pauta 

● Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por
um tempo determinado na chamada de abertura ou 1 minuto por default)

● Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado
é identificado por um id único e pode votar apenas uma vez por pauta)

● Contabilizar os votos e dar o resultado da votação na pauta

É importante que as pautas e os votos sejam persistidos e que não sejam perdidos com o restart
da aplicação.


## Como executar

Subir o docker compose na pasta docker:
``` cd docker && docker compose up -d ```

Apos isso, subir a aplicação com o comando:
``` gradle bootRun ```

## Como testar

A aplicação possui uma collection do Postman para testes, que pode ser importada para realizar as validaçõe dos endpoints.

## Tecnologias utilizadas
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- MongoDB
- Docker
- Postman
- JUnit 5
- Mockito
- Lombok

## Estrutura do projeto
A estrutura do projeto segue o padrão MVC (Model-View-Controller) e está organizada da seguinte forma:
```
docker                                          - arquvo docker compose para inicializacao do ambiente
collection                                      - arquivo Postman para testes dos endpoints
src
├── main
│   ├── java
│   │   └── com
│       └── assessnment
│           └── voting
|               ├── client                      - clients de acesso para servicos externos 
│               ├── controller                  - controladores REST para gerenciar as pautas e votos
│               ├── dto                         - objetos de transferência de dados (DTOs) para comunicação entre camadas
│               ├── exception                   - exceções personalizadas para tratamento de erros
│               ├── model                       - modelos de dados (entidades) para pautas e votos
│               ├── repository                  - repositórios para acesso aos dados
│               ├── service                     - serviços para lógica de negócios
│               ├── util                        - utilitários para formatação e manipulação de dados
│   └── resources
│       ├── db.migration                        - scripts de migração do banco de dados
│       ├── application.yml                     - arquivo de configuração da aplicação
│       └── log4j2-spring.xml                   - configuração do Log4j2 para logging
└── test
    └── java
        └── com
            └── assessnment
                └── voting
                    ├── controller
                    ├── service
```
## Considerações Finais
A escolha das tecnologias foi feita visando a escalabilidade e manutenção do sistema.

Nesse sentido esta usando Webflux para aumentar a performance da aplicação. Na persistencia esta se utilzando PostgreSQL para armazenar as pautas e os associados, visto que o PostgreSQL é um banco de dados relacional robusto e confiável,
E para a experiencia mobile, foi criada uma versao diferente, com as telas sendo salvas em um mongoDB, permitindo uma maior flexibilidade 
e integrando com as funcionalidades desenvolvidas para votação.

Foi feita a validação de cpf através do endpoint https://user-info.herokuapp.com/users/{cpf}, mas no momento da implementação este estava 
indisponivel, então foi criado um Circuito para redirecionar para um metodo que retorna aleatoriamente.

Os testes de integração utilizam um banco em memória H2 para simular o ambiente de produção, garantindo que as funcionalidades sejam testadas sem a necessidade de um banco de dados real.

Foram criadas 2 versões do endpoint de votação, uma para a versão mobile e outra para a versão web, permitindo que a aplicação atenda a diferentes necessidades de usuários,
sendo a versão mobile retornado informações das telas que serão utilizadas, e a versão web mais enxuta, contendo apenas as informações necessárias para a votação.