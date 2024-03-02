package me.insiro.home.server.file.repository

import me.insiro.home.server.file.vo.FileItemFactory
import me.insiro.home.server.file.vo.VOFileCollection
import me.insiro.home.server.file.vo.VOTextFileItem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Description
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory

class StaticFileRepositoryTest {
    private lateinit var repository: StaticFileRepository
    private lateinit var testDirectoryPath: String
    private lateinit var tempPath: Path

    private fun getCollectionInstance(): VOFileCollection {
        return VOFileCollection("domain", "collection")
    }

    @BeforeEach
    fun init() {
        tempPath = createTempDirectory("test")
        testDirectoryPath = tempPath.toAbsolutePath().toString()
        repository = StaticFileRepository(testDirectoryPath)
    }

    @Test
    @Description("test for save, load")
    fun testSaveAndLoadTextFile() {
        val collection = getCollectionInstance()
        val fileName = "testFile.txt"
        val content = "test Content"
        val fileVO = VOTextFileItem(collection, fileName)
        val savedFile = repository.save(fileVO, content)
        val loadedFile = repository.load(savedFile)
        assertNotNull(loadedFile)
        assertEquals(content, loadedFile!!.content)
    }


    @Test
    fun getCollections() {
        val domain = "test-domain"
        val collection1 = VOFileCollection(domain, "collection1")
        val collection2 = VOFileCollection(domain, "collection2")

        tempPath.resolve(domain).resolve(collection1.collection).createDirectories()
        tempPath.resolve(domain).resolve(collection2.collection).createDirectories()
        val collections = repository.getCollections(domain)
        assertEquals(setOf(collection1, collection2), collections.toSet())

    }

    @Test
    fun find() {
        val collection = getCollectionInstance()

        val file1 = FileItemFactory.new(collection, "file1.txt")
        val file2 = FileItemFactory.new(collection, "file2.png")
        val multipart = MockMultipartFile("mock", "Test String".toByteArray())
        repository.save(file1, "")
        repository.save(file2, multipart)

        val items = repository.find(collection)
        assertEquals(setOf(file1, file2), items.toSet())


    }

    @Test
    fun append() {
        val collection = getCollectionInstance()

        val file1 = FileItemFactory.new(collection, "file1.txt")
        val saved = repository.save(file1, "data") as VOTextFileItem
        val appendedTxt = "\nappend"
        repository.append(saved, appendedTxt)
        val appended = repository.load(saved)
        assertNotNull(appended)
        assertEquals(saved.content + appendedTxt, appended!!.content)
    }

    @Test
    fun delete() {
        val collection = getCollectionInstance()
        val file1 = FileItemFactory.new(collection, "file1.txt")
        val saved = repository.save(file1, "data")
        repository.delete(saved)
        assertNull(repository.get(saved))
    }
}