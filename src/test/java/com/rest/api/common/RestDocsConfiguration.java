package com.rest.api.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;


import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@TestConfiguration  // 테스트 에서만 사용하는 Configuration
public class RestDocsConfiguration {

	@Bean
	public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcBuilderCustomizer() {
		return new RestDocsMockMvcConfigurationCustomizer() {
			
			@Override
			public void customize(MockMvcRestDocumentationConfigurer configurer) {
				configurer.operationPreprocessors()
					.withRequestDefaults(prettyPrint())
					.withResponseDefaults(prettyPrint());
			}
		};
	}
	
	
}
