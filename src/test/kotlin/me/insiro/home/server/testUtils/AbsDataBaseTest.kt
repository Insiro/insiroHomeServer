package me.insiro.home.server.testUtils

import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Comments
import me.insiro.home.server.post.entity.Posts
import me.insiro.home.server.project.entity.ProjectTypeRelations
import me.insiro.home.server.project.entity.ProjectTypes
import me.insiro.home.server.project.entity.Projects
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
abstract class AbsDataBaseTest(private vararg val tables: Table) {

    protected val source = TestDataSource
    protected open fun resetDataBase() = transaction {
        //region ordering to drop
        SchemaUtils.drop(Comments)
        SchemaUtils.drop(Posts)
        SchemaUtils.drop(Categories)
        SchemaUtils.drop(ProjectTypeRelations)
        SchemaUtils.drop(ProjectTypes)
        SchemaUtils.drop(Projects)
        SchemaUtils.drop(Users)
        //endregion
            SchemaUtils.create(*tables)
    }
}
