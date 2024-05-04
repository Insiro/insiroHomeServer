package me.insiro.home.server.file.config

import me.insiro.home.server.file.controller.fileServer.IFileServer
import me.insiro.home.server.file.controller.fileServer.StaticFileServer
import me.insiro.home.server.file.repository.IFileRepository
import me.insiro.home.server.file.repository.StaticFileRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FileConfig(
    private val configProperties: FileConfigProperties
) {


    @Bean
    fun fileRepository():IFileRepository{
        return when(configProperties.storage){
          FileConfigProperties.FileStorage.Local-> StaticFileRepository(configProperties.location)
            else -> throw Exception("Wong File Configuration Exception")
        }
    }
    @Bean
    fun fileServer(): IFileServer<*> {
        return when(configProperties.storage){
            FileConfigProperties.FileStorage.Local-> StaticFileServer(configProperties.location)
            else -> throw Exception("Wong File Configuration Exception")
        }
    }

}