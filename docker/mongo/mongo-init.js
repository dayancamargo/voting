db = db.getSiblingDB('admin');
// move to the admin db - always created in Mongo
db.auth("root", "pass");
// log as root admin if you decided to authenticate in your docker-compose file...
db = db.getSiblingDB('vote-db');
// create and move to your new database
db.createUser({
'user': "userVote",
'pwd': "pass",
'roles': [{
    'role': 'dbOwner',
    'db': 'vote-db'}]});
// user created
db.createCollection('mobile_screen');


db.getCollection('mobile_screen').insertMany(
[
    {
        "_id": "FORM_CRIA_PAUTA",
        "tipo": "FORMULARIO",
        "titulo": "CRIACAO DE PAUTA",
        "itens": [
            {
                "tipo": "TEXTO",
                "texto": "Criaçao de uma nova pauta"
            },
            {
                "tipo": "INPUT_TEXTO",
                "id": "idNomeDaPauta",
                "titulo": "Nome da pauta",
                "valor": "Nome"
            }
        ],
        "botaoOk": {
            "texto": "Criar",
            "url": "v2/agenda/criar"
        },
        "botaoCancelar": {
            "texto": "Cancelar",
            "url": "v2/agenda"
        }
    },
    {
        "_id": "FORM_INICIA_PAUTA",
        "tipo": "FORMULARIO",
        "titulo": "INICIA A PAUTA",
        "itens": [
            {
                "tipo": "TEXTO",
                "texto": "Inicia a sessao de votacao de uma pauta"
            },
            {
                "tipo": "INPUT_NUMERO",
                "id": "idAgenda",
                "titulo": "Identificador da agenda",
                "valor": ""
            },
            {
                "tipo": "INPUT_TEXTO",
                "id": "idNomeAgenda",
                "titulo": "Nome da agenda",
                "valor": ""
            },
            {
                "tipo": "INPUT_NUMERO",
                "id": "quantidadeDeTempo",
                "titulo": "Quantidade de tempo",
                "valor": ""
            },
            {
                "tipo": "INPUT_TEXTO",
                "id": "unidadeTempo",
                "titulo": "Unidade de tempo",
                "valor": ""
            }
        ],
        "botaoOk": {
            "texto": "Criar",
            "url": "/v2/agenda/abrir-sessao"
        },
        "botaoCancelar": {
            "texto": "Cancelar",
            "url": "/v2/agenda"
        }
    },
    {
        "_id": "FORM_VOTAR",
        "tipo": "FORMULARIO",
        "titulo": "VOTA EM UMA PAUTA",
        "itens": [
            {
                "tipo": "TEXTO",
                "texto": "Permite um cpf votar em uma pauta"
            },
            {
                "tipo": "INPUT_NUMERO",
                "id": "idAgenda",
                "titulo": "Identificador da pauta",
                "valor": ""
            },
            {
                "tipo": "INPUT_TEXTO",
                "id": "idCpf",
                "titulo": "Cpf do associado",
                "valor": ""
            },
            {
                "tipo": "INPUT_TEXTO",
                "id": "idVoto",
                "titulo": "Voto da pauta",
                "valor": ""
            }
        ],
        "botaoOk": {
            "texto": "Votar",
            "url": "/v2/vote/votar"
        },
        "botaoCancelar": {
            "texto": "Cancelar",
            "url": "/v2/agenda"
        }
    },
    {
        "_id": "SEL_PAUTA",
        "tipo": "SELECAO",
        "titulo": "Lista de pautas",
        "itens": [
            {
                "texto": "Nome da pauta",
                "url": "v2/agenda/id",
                "body": {
                    "id": ""
                }
            }
        ]
    },
    {
        "_id": "FORM_TOTAL",
        "tipo": "FORMULARIO",
        "titulo": "TOTAL DE VOTOS EM UMA PAUTA",
        "itens": [
            {
                "tipo": "TEXTO",
                "texto": "Mostra a quantidade de votos de uma pauta"
            },
            {
                "tipo": "INPUT_TEXTO",
                "id": "idNomeAgenda",
                "titulo": "Nome da pauta",
                "valor": ""
            },
            {
                "tipo": "TEXTO",
                "id": "idSim",
                "texto": "Quantidade de SIM: "
            },
            {
                "tipo": "TEXTO",
                "id": "idNao",
                "texto": "Quantidade de NÃO: "
            }
        ],
        "botaoCancelar": {
            "texto": "Voltar",
            "url": "/v2/agenda"
        }
    }
]);