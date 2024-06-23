package me.insiro.home.server.project.service

import me.insiro.home.server.project.dto.type.ModifyProjectTypeDTO
import me.insiro.home.server.project.entity.*
import me.insiro.home.server.project.repository.ProjectTypeRepository
import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.testUtils.DBInserter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectTypeServiceTest : AbsDataBaseTest(Projects, ProjectTypes, ProjectTypeRelations) {
    private val typeRepository = ProjectTypeRepository()
    private lateinit var service: ProjectTypeService
    private lateinit var project: Project
    private lateinit var type: ProjectType

    @BeforeEach
    fun initTest() {
        resetDataBase()
        service = ProjectTypeService(typeRepository)
        project = DBInserter.insertProject(Project.Raw("title"))
        type = DBInserter.insertProjectType(ProjectType("TYPE"))
        DBInserter.insertProjectTypeRelation(project, type)
    }

    @Test
    fun `test find`() {
        val result = service.find()
        assertEquals(1, result.size)
        assertEquals(type.name, result.first().name)
    }

    @Test
    fun `test create and get`() {
        val typeName = "testType1"
        val created = service.create(ModifyProjectTypeDTO(typeName, false)).getOrThrow()
        val found = service.get(created.id!!).getOrThrow()
        assertEquals(found.name, created.name)
    }

    @Test
    fun `test Update`() {
        val modifyDTO = ModifyProjectTypeDTO("NEW_TYPE", false)
        val updated = service.update(type.id!!, modifyDTO).getOrNull()
        val found = service.find().first()
        assertNotNull(updated)
        assertEquals(found.name, updated!!.name)
        assertEquals(modifyDTO.name, updated.name)
    }

    @Test
    fun `test delete`() {
        service.delete(type.id!!)
        val found = service.find()
        assertEquals(0, found.size)
    }

}