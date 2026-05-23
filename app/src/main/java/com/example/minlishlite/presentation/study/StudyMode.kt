package com.example.minlishlite.presentation.study

sealed class StudyMode {
    data class DeckDue(val deckId: Int) : StudyMode()
    data object DueToday : StudyMode()
}
