package me.insiro.home.server.post.entity

import jdk.jshell.Snippet.Status
import me.insiro.home.server.application.domain.EntityVO

data class Post(
        var title: String,
        var status: Status,
        var authorId: Long,
        var category: Int?,
        val fileName: String,
) : EntityVO<Long>()