package me.insiro.home.server.file.service

import AbsFileService
import me.insiro.home.server.file.repository.IFileRepository
import me.insiro.home.server.post.entity.Post
import org.springframework.stereotype.Service

@Service
class PostFileService(fileRepository: IFileRepository) : AbsFileService<Post>("post", fileRepository)