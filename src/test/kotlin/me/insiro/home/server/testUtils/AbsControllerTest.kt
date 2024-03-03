package me.insiro.home.server.testUtils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.insiro.home.server.application.domain.EntityVO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(MockitoExtension::class)
abstract class AbsControllerTest(private val baseUrl: String) {
    protected lateinit var mockMvc: MockMvc

    companion object {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    }

    @BeforeEach
    abstract fun init()

    protected val uri:String
        get() {return baseUrl}
    fun uri(child: Any? = null): String {
        child ?: return baseUrl
        val childString = if( child is EntityVO.Id<*>)child.value.toString() else child.toString()
        return "$baseUrl/$childString"
    }
}