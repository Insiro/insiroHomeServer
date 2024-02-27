package me.insiro.home.server.file.repository

import me.insiro.home.server.file.entity.File


abstract class AbsFileRepository {
    abstract fun find(headerVO: File.FileHeaderVO): File?
    abstract fun findAll(): List<File.FileHeaderVO>
    abstract fun findAllByName(fileName: String): List<File.FileHeaderVO>?
    abstract fun findAllByDomain(domain: String): List<File.FileHeaderVO>?
    abstract fun save(contentVO: File): File

    abstract fun update(contentVO: File.FileContentVO): File.FileContentVO
    abstract fun delete(headerVO: File.FileHeaderVO)

}