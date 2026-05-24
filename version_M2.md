# PokemonApp

  Aplicação Pokédex Multiplatform evoluída da M1 para a M2 com foco em arquitetura, estado reativo, consumo de API real e persistência local.

  ## Objetivo desta versão

  Esta entrega substitui a camada de dados mockada por uma arquitetura híbrida com:

  - consumo real da PokeAPI;
  - persistência local com Room KMP;
  - gerenciamento de estado com `ViewModel`;
  - fluxo reativo de UI com `StateFlow`;
  - estratégia `Offline-First` para a listagem;
  - persistência de favoritos/time com local de captura.

  ## Principais mudanças implementadas

  ### 1. Arquitetura e gerenciamento de estado

  A lógica de negócio deixou de ficar acoplada diretamente às telas e passou a ser centralizada em um `ViewModel`.

  Foi implementado:

  - `PokemonViewModel` como fonte principal de estado da UI;
  - `UiState` com os estados:
    - `Loading`
    - `Success`
    - `Error`
  - consumo desses estados pelas telas com fluxo reativo.

  **Arquivos principais:**
  - `PokemonViewModel.kt`
  - `UiState.kt`
  - `App.kt`

  ---

  ### 2. Consumo de API real

  O repositório mockado da M1 foi removido e a aplicação passou a consumir dados reais da PokeAPI com Ktor.

  Foi implementado:

  - cliente HTTP com Ktor;
  - models de resposta da API;
  - busca da listagem base dos Pokémons;
  - busca dos detalhes em tempo real ao abrir a tela de detalhes.

  **Arquivos principais:**
  - `PokemonRepository.kt`
  - `PokeApiModels.kt`

  ---

  ### 3. Estratégia Offline-First

  A listagem base dos Pokémons passou a seguir a estratégia offline-first.

  Funcionamento:

  - na primeira inicialização, o app busca nomes e IDs da PokeAPI;
  - esses dados são persistidos na tabela local `pokemon_cache`;
  - nas inicializações seguintes, o app reaproveita a base local e evita chamadas redundantes.

  **Arquivos principais:**
  - `PokemonRepository.kt`
  - `PokemonDao.kt`
  - `PokemonEntity.kt`

  ---

  ### 4. Persistência local com Room KMP

  Foi criada uma base local multiplataforma usando Room KMP.

  Estrutura criada:

  - tabela de cache da Pokédex;
  - tabela de favoritos/time;
  - builders de banco para Android e iOS.

  **Arquivos principais:**
  - `AppDatabase.kt`
  - `AppDatabase.android.kt`
  - `AppDatabase.ios.kt`
  - `PokemonDao.kt`
  - `PokemonEntity.kt`

  ---

  ### 5. Paginação na listagem principal

  A Pokédex não carrega todos os registros de uma vez.

  Foi implementado:

  - carregamento paginado com `LIMIT` e `OFFSET`;
  - carregamento sob demanda conforme o scroll da tela;
  - acúmulo progressivo dos itens na UI.

  **Arquivos principais:**
  - `PokemonViewModel.kt`
  - `PokemonRepository.kt`
  - `PokemonDao.kt`
  - `PokedexScreen.kt`

  ---

  ### 6. Filtros integrados ao banco local

  A busca e os filtros passaram a atuar diretamente sobre o banco local.

  Foi implementado:

  - busca por nome;
  - filtro por tipo;
  - integração entre filtros e paginação.

  **Arquivos principais:**
  - `PokemonDao.kt`
  - `PokemonRepository.kt`
  - `PokedexScreen.kt`

  ---

  ### 7. Detalhes com consulta HTTP em tempo real

  Ao abrir um Pokémon, a aplicação não lê os detalhes completos do banco local.

  Em vez disso:

  - faz uma requisição HTTP direta para a PokeAPI;
  - carrega dados atualizados no momento da navegação;
  - exibe informações detalhadas na tela.

  **Arquivos principais:**
  - `PokemonRepository.kt`
  - `PokemonViewModel.kt`
  - `DetailsScreen.kt`

  ---

  ### 8. Favoritos/time com persistência definitiva

  A funcionalidade de salvar Pokémons no time/favoritos passou a ser persistida no dispositivo.

  Foi implementado:

  - inserção no banco local;
  - leitura da lista persistida;
  - remoção de Pokémons salvos.

  **Arquivos principais:**
  - `PokemonRepository.kt`
  - `PokemonDao.kt`
  - `PokemonEntity.kt`
  - `PokemonViewModel.kt`

  ---

  ### 9. Nova regra de negócio: local de captura

  Ao favoritar ou adicionar um Pokémon ao time, o app agora exige a informação de onde ele foi capturado.

  Foi implementado:

  - coleta do local de captura na tela de detalhes;
  - persistência desse valor junto ao Pokémon salvo.

  **Arquivos principais:**
  - `DetailsScreen.kt`
  - `PokemonRepository.kt`
  - `PokemonEntity.kt`
  - `Pokemon.kt`

  ---

  ### 10. Integração nas plataformas

  A inicialização da aplicação foi ajustada para injetar o banco de dados nas duas plataformas.

  Também foi adicionada:

  - permissão de internet no Android para permitir acesso à PokeAPI.

  **Arquivos principais:**
  - `MainActivity.kt`
  - `MainViewController.kt`
  - `AndroidManifest.xml`
  - `App.kt`
