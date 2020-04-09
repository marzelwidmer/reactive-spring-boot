package ch.keepcalm.demo.http

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpServiceApplicationTests {

	@Test
	fun contextLoads() {
	}

}
