package com.rest.api;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.common.RestDocsConfiguration;

/**
 * 
 * 테스트 코드 리팩토링
 * @author lee
 *
 */
//@WebMvcTest // 웹 과 관련된 빈들이 모두 등록 됨.  웹용 빌드들만 빈에등록해줌
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc  // @SpringBootTest 사용시 mocmvc 쓸려면 이거 써야됨/
@AutoConfigureRestDocs // restdocs 사용하기 위한 어노테이션
@Import(RestDocsConfiguration.class) //다른 스프링 빈설정을 읽어와서 사용하는 방법
@ActiveProfiles("test") //test 프로파일로 프로파일로 실행하겠다. 기본적인 application.properties랑 test application.properties 사용하게된다. 
@Ignore // 테스트를 가지고 있는 클래스로 간주되지않게 하기 위해
public class BaseControllerTest {
	
	@Autowired
	protected MockMvc mockMvc; // 가짜 요청을 만들어서 디스패처 서블릿을 만들어서 요청 및 응답 받을 수 있다. 웹서버를 만들지않아서 빠름. repository를 등록해주지않음.
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected ModelMapper modelMapper;
}
