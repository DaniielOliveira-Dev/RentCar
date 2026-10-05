# RentCar

Aplicativo Android nativo desenvolvido em Kotlin para gestão simples de locação de veículos.

Projeto acadêmico da disciplina **Desenvolvimento de Sistemas para Dispositivos Móveis**.

## Tecnologias utilizadas

- Kotlin
- Jetpack Compose + Material 3
- MVVM
- StateFlow + `collectAsStateWithLifecycle()`
- Room Database 3
- Navigation Compose
- Kotlin Coroutines e Flow
- ContentResolver + ContactsContract
- Runtime Permission (`READ_CONTACTS`)
- Retrofit 2 + Gson

## Funcionalidades

### Dashboard
- Tela inicial do aplicativo.
- Lista somente locações com status `ATIVA`.
- Exibe marca, modelo, placa, cliente, telefone, data de saída e entrega prevista.
- Calcula os dias restantes com base na data atual.
- Destaca visualmente locações em atraso.
- FAB para criar nova locação.
- Permite finalizar uma locação, liberando o veículo e enviando a locação ao histórico.

### Veículos
- Lista a frota e o status atual (`DISPONIVEL`, `ALUGADO` ou `MANUTENCAO`).
- Cadastro com marca, modelo, placa, ano e valor da diária.
- Validação de placa antiga (`AAA-1234`) e Mercosul (`AAA1A23`).
- Validação de valor positivo e campos obrigatórios.
- Novo veículo é salvo como `DISPONIVEL`.

### Contatos
- Solicita `READ_CONTACTS` em tempo de execução.
- Exibe orientação e opção de tentar novamente se a permissão for negada.
- Busca os contatos do dispositivo usando `ContentResolver` e `ContactsContract`.
- Filtra contatos por nome em tempo real.
- Retorna ID, nome e telefone usando `SavedStateHandle`.

### Nova locação
- Exibe somente veículos `DISPONIVEL`.
- Seleciona cliente a partir da agenda.
- Usa `DatePicker` para saída e entrega prevista.
- Calcula automaticamente dias, diária e total estimado.
- Confirmação salva a locação como `ATIVA` e altera o veículo para `ALUGADO` dentro de uma transação Room.
- Retorna ao Dashboard após salvar.

### Histórico
- Lista as locações finalizadas.

## Banco de dados

O Room possui três tabelas principais:

- `vehicles`: veículos da frota.
- `clients`: cache dos clientes selecionados da agenda.
- `rentals`: locações.

`rentals.vehicleId` possui chave estrangeira para `vehicles.id` e `rentals.clientId` possui chave estrangeira para `clients.id`.

A classe `RentalWithDetails` utiliza `@Relation` para reunir locação, veículo e cliente.

## Arquitetura

```text
UI (Jetpack Compose)
        |
        v
ViewModels (StateFlow)
        |
        v
Repositories
   /       |       \
Room   Contacts   Retrofit
```

Organização principal:

```text
com.danieloliveira.rentcar
├── data
│   ├── contacts
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   └── relation
│   ├── remote
│   └── repository
├── domain
│   ├── model
│   ├── util
│   └── validator
├── navigation
└── ui
    ├── contact
    ├── dashboard
    ├── history
    ├── rental
    └── vehicle
```

## Retrofit

O Retrofit 2 está configurado com um endpoint REST público usado apenas como **mock de sincronização** para a atividade. O banco Room continua sendo a fonte local de dados do aplicativo. Falhas de rede não anulam os registros locais.

## Como executar

1. Abra a pasta do projeto no Android Studio.
2. Aguarde o Gradle Sync terminar.
3. Selecione um emulador ou dispositivo Android.
4. Clique em **Run**.
5. Para testar contatos no emulador, adicione pelo menos um contato ao aplicativo de Contatos do Android.

O projeto utiliza `minSdk = 26`.

## Observação sobre Room 3
O projeto usa `AndroidSQLiteDriver`, exigido pelo Room 3 para criação do banco no Android.
