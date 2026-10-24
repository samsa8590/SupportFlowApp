package io.github.samsa8590.supportFlow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SupportFlowApplicationTests {

	@Test
	void contextLoads() {
	}

}
