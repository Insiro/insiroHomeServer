package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.EntityVO


data class Project(
        var title: String, var status: String, var fileName: String,
) : EntityVO<Long>()