package me.insiro.home.server.application.domain

import java.time.LocalDateTime

interface IResponseDTO<T> {
    val id:T
    val createdAt: LocalDateTime?
}