package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.EntityVO


data class ProjectType(var name: String) : EntityVO<Int>()

