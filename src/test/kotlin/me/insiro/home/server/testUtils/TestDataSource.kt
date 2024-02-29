package me.insiro.home.server.testUtils

import org.jetbrains.exposed.sql.Database

object TestDataSource {
    private const val URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
    private const val DRIVER = "org.h2.Driver"
    private const val USER_NAME = "sa"
    private const val PASSWORD = ""
    val db = Database.connect(URL, driver = DRIVER, user = USER_NAME, password = PASSWORD)
}