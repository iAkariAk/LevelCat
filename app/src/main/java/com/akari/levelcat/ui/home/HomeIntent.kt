package com.akari.levelcat.ui.home

sealed interface HomeIntent {
    enum class SelectCreateProjectMode : HomeIntent {
        New, Clipboard, System
    }
    sealed interface CreateProject : HomeIntent {
        data class New(val name: String, val creator: String) : CreateProject
        data object Clipboard : CreateProject
        data object Saf : CreateProject
    }
}