package me.insiro.home.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.web.servlet.MockMvc
import java.net.URI

@ExtendWith(MockitoExtension::class)
abstract class AbsControllerTest(private val baseUrl: String) {
    protected lateinit var mockMvc: MockMvc

    companion object {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    }

    @BeforeEach
    abstract fun init()
    fun uri(child: Any? = null): String {
        child ?: return baseUrl
        return URI.create(baseUrl).resolve(child.toString()).toString()
    }
}