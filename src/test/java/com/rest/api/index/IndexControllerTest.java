package com.rest.api.index;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.rest.api.common.RestDocsConfiguration;

//@WebMvcTest // 웹 과 관련된 빈들이 모두 등록 됨.  웹용 빌드들만 빈에등록해줌
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc  // @SpringBootTest 사용시 mocmvc 쓸려면 이거 써야됨/
@AutoConfigureRestDocs // restdocs 사용하기 위한 어노테이션
@Import(RestDocsConfiguration.class) //다른 스프링 빈설정을 읽어와서 사용하는 방법
@ActiveProfiles("test") //test 프로파일로 프로파일로 실행하겠다. 기본적인 application.properties랑 test application.properties 사용하게된다. 
public class IndexControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Test
	public void index() throws Exception {
		this.mockMvc.perform(get("/api/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("_links.events").exists());
	}
}
