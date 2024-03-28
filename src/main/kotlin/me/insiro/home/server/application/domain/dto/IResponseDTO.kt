package me.insiro.home.server.application.domain.dto

import java.time.LocalDateTime

interface IResponseDTO<T> {
    val id:T
    val createdAt: LocalDateTime?
}