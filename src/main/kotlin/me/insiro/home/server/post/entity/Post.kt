package me.insiro.home.server.post.entity

import jdk.jshell.Snippet.Status
import me.insiro.home.server.application.domain.BaseEntityVO
import me.insiro.home.server.application.domain.EntityVO
import org.jetbrains.exposed.dao.id.EntityID

data class Post(
    var title: String,
    var status: Status,
    var authorId: Long,
    var category: Int?,
    val fileName: String,
    override val id: EntityVO.Id<Long>?,
) : BaseEntityVO() {
    @JvmInline
    value class Id(override val value: Long) : BaseEntityVO.Id {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}
