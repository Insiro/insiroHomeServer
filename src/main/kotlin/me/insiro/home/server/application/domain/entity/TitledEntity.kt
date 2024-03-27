package me.insiro.home.server.application.domain.entity

import org.jetbrains.exposed.sql.Column

interface ITitledTable{
    val title: Column<String>
}

interface TitledVO {
    val title:String
}