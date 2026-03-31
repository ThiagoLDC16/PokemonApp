package com.example.pokemonapp.data

object PokemonRepository {
    private val pokemonList = listOf(
        Pokemon(
            1, "Bulbasaur", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
            listOf("Grass", "Poison"), "A strange seed was planted on its back at birth. The plant sprouts and grows with this POKéMON.",
            45, 49, 49, 45
        ),
        Pokemon(
            4, "Charmander", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
            listOf("Fire"), "Obviously prefers hot places. When it rains, steam is said to spout from the tip of its tail.",
            39, 52, 43, 65
        ),
        Pokemon(
            7, "Squirtle", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
            listOf("Water"), "After birth, its back swells and hardens into a shell. Powerfully sprays foam from its mouth.",
            44, 48, 65, 43
        ),
        Pokemon(
            25, "Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
            listOf("Electric"), "When several of these POKéMON gather, their electricity could build and cause lightning storms.",
            35, 55, 40, 90
        ),
        Pokemon(
            133, "Eevee", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/133.png",
            listOf("Normal"), "It has an irregular genetic code that allows it to evolve if exposed to radiation from element STONES.",
            55, 55, 50, 55
        ),
        Pokemon(
            94, "Gengar", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/94.png",
            listOf("Ghost", "Poison"), "Under a full moon, this POKéMON likes to mimic the shadows of people and laugh at their fright.",
            60, 65, 60, 110
        ),
        Pokemon(
            149, "Dragonite", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/149.png",
            listOf("Dragon", "Flying"), "An extremely rarely seen marine POKéMON. Its intelligence is said to match that of humans.",
            91, 134, 95, 80
        ),
        Pokemon(
            150, "Mewtwo", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/150.png",
            listOf("Psychic"), "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
            106, 110, 90, 130
        ),
        Pokemon(
            212, "Scizor", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/212.png",
            listOf("Bug", "Steel"), "It has a body with the hardness of steel. It is not easily fazed by ordinary attacks.",
            70, 130, 100, 65
        ),
        Pokemon(
            448, "Lucario", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/448.png",
            listOf("Fighting", "Steel"), "It has the ability to sense the auras of all things. It understands human speech.",
            70, 110, 70, 90
        )
    )

    fun getPokemonList(): List<Pokemon> = pokemonList

    fun getPokemonById(id: Int): Pokemon? = pokemonList.find { it.id == id }
}
