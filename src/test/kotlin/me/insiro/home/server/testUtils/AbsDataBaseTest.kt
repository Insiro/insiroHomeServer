package me.insiro.home.server.testUtils

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
abstract class AbsDataBaseTest(private val tables: List<Table>) {
    protected val source = TestDataSource
    protected fun resetDataBase() = transaction {
        for (table in tables) {
            print(table.tableName)
            SchemaUtils.drop(table)
            SchemaUtils.create(table)
        }
    }
}