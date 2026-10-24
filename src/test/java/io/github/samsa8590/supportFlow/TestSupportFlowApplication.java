package io.github.samsa8590.supportFlow;

import org.springframework.boot.SpringApplication;

public class TestSupportFlowApplication {

	public static void main(String[] args) {
		SpringApplication.from(SupportFlowApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
