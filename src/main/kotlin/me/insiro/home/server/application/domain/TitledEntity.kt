package me.insiro.home.server.application.domain

abstract class TitledTable:BaseIDTable() {
    val title = varchar("title", 100)
}

interface TitledVO:IBaseEntityVO{
    val title:String
}