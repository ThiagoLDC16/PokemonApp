# Atividade de Programação para Dispositivos Móveis II - PokemonApp

PokemonApp é uma aplicação Pokédex multiplataforma desenvolvida com Kotlin Multiplatform e Compose Multiplatform. O projeto oferece listagem de Pokémons, tela de detalhes com dados em tempo real da PokeAPI e
persistência local para time/favoritos.

## Visão Geral

O projeto foi estruturado com uma arquitetura moderna, baseada em:

- gerenciamento reativo de estado com `ViewModel` e `StateFlow`
- persistência local com Room KMP
- consumo de API com Ktor
- estratégia offline-first para a listagem principal
- compartilhamento de lógica entre Android e iOS

A aplicação mantém a listagem base em banco local, pagina os resultados a partir do cache e consulta os detalhes do Pokémon sob demanda ao abrir a tela de detalhes.

## Funcionalidades

- Navegação pela Pokédex com carregamento paginado
- Busca de Pokémons por nome
- Filtro por tipo
- Tela de detalhes com dados carregados em tempo real da PokeAPI
- Salvamento de Pokémons no time/favoritos
- Registro do local de captura ao salvar um Pokémon
- Persistência local dos dados salvos
- Reaproveitamento da mesma lógica de negócio em Android e iOS

## Autores
- Thiago Lima de Córdova
- Felipe Galkowski
