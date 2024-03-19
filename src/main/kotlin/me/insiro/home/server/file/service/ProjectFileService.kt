package me.insiro.home.server.file.service

import AbsFileService
import me.insiro.home.server.file.repository.IFileRepository
import me.insiro.home.server.project.entity.Project


class ProjectFileService(repository: IFileRepository) : AbsFileService<Project>("project", repository)