package com.akari.levelcat.ui.home

sealed interface HomeIntent {
    data class CreateProject(val name: String, val creator: String) : HomeIntent
}