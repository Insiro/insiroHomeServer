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
    fun uri(vararg children: Any): String {
        val strBuilder = StringBuilder(baseUrl)
        for (child in children){
            if (child is EntityVO.Id<*>)
                strBuilder.append("/${child.value}")
            else
                strBuilder.append("/$child")
        }
        return strBuilder.toString()
    }
}