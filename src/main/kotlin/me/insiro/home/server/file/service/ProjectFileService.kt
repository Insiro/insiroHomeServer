package me.insiro.home.server.file.service

import me.insiro.home.server.file.repository.IFileRepository
import me.insiro.home.server.project.entity.Project
import org.springframework.stereotype.Service

@Service
class ProjectFileService(repository: IFileRepository) : AbsFileService<Project>("projects", repository){

    override fun collectionName(vo: Project): String {
        return vo.title
    }
}