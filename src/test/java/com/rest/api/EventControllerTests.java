package com.rest.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.common.TestDescription;
import com.rest.api.event.Event;
import com.rest.api.event.EventDto;
import com.rest.api.event.EventStatus;


//@WebMvcTest // 웹 과 관련된 빈들이 모두 등록 됨.  웹용 빌드들만 빈에등록해줌
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc  // @SpringBootTest 사용시 mocmvc 쓸려면 이거 써야됨/
public class EventControllerTests {

	// moc mvc 주입 받아서 사용가능
	@Autowired
	MockMvc mockMvc; // 가짜 요청을 만들어서 디스패처 서블릿을 만들어서 요청 및 응답 받을 수 있다. 웹서버를 만들지않아서 빠름. repository를 등록해주지않음.
	
	@Autowired
	ObjectMapper objectMapper;
	
//	@MockBean //repository에 해당하는 빈을 목으로 만들어 달라.
//	EventRepository eventRepository;

	@Test
	@TestDescription("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {
		EventDto event = EventDto.builder()
				.name("String")
				.description("REST API")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14, 4, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 13, 14, 4, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 14, 14, 4, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 12, 14, 4, 21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남")
				.build();
		
//		event.setId(10);
//		Mockito.when(eventRepository.save(event)).thenReturn(event); // eventRepository가 호출이 되면 event 객체를 return 해라.
		
		
	 // perform안에 주는게 요청
		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON) // 요청에 본문에 JSON을 담아서 보내고 있다고 알려줌
				.accept(MediaTypes.HAL_JSON)  //내가원하는 요청은 HAL 응답을 받고싶다.
				.content(objectMapper.writeValueAsString(event))) // objectMapper json 문자열로 바꿔서 요청본문에 요청
			  .andDo(print()) // 응답 확인 하기 위해
			  .andExpect(status().isCreated())
			  .andExpect(jsonPath("id").exists())
			  .andExpect(header().exists(HttpHeaders.LOCATION))
			  .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
			  .andExpect(jsonPath("id").value(Matchers.not(100)))
			  .andExpect(jsonPath("free").value(Matchers.not(true)))
			  .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
	}
	
	@Test
	@TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
	public void createBadRequest() throws Exception {
		Event event = Event.builder()
				.id(100)
				.name("String")
				.description("REST API")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14, 4, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 13, 14, 4, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 14, 14, 4, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 12, 14, 4, 21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남")
				.free(true)
				.offline(false)
				.eventStatus(EventStatus.PUBLISHED)
				.build();
		
//		event.setId(10);
//		Mockito.when(eventRepository.save(event)).thenReturn(event); // eventRepository가 호출이 되면 event 객체를 return 해라.
		
		
	 // perform안에 주는게 요청
		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON) // 요청에 본문에 JSON을 담아서 보내고 있다고 알려줌
				.accept(MediaTypes.HAL_JSON)  //내가원하는 요청은 HAL 응답을 받고싶다.
				.content(objectMapper.writeValueAsString(event))) // objectMapper json 문자열로 바꿔서 요청본문에 요청
			  .andDo(print()) // 응답 확인 하기 위해
			  .andExpect(status().isBadRequest()); // 응답값
	}
	
	@Test
	@TestDescription("입력 받을 수 없는 값이 비어 있는 경우 발생하는 테스트")
	public void createEventBadRequestEmptyInput() throws Exception {
		EventDto eventDto = EventDto.builder().build();
		
		this.mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON) // json 보내는 content 
				.accept(this.objectMapper.writeValueAsString(eventDto)))  // object로변환해서 보냄
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트") //junit5 에서 테스트로 작성하면 이목록들이 작성한 이름으로 나옴 junit은 메서드 이름으로 나옴.
	public void createEventBadRequestWrongInput() throws Exception {
		EventDto event = EventDto.builder()
				.name("String")
				.description("REST API")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 14, 14, 4, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14, 4, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 14, 14, 4, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 13, 14, 4, 21))
				.basePrice(100000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남")
				.build();
		
		this.mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON) // json 보내는 content 
				.accept(MediaTypes.HAL_JSON_VALUE) 
				.content(this.objectMapper.writeValueAsString(event)))  // object로변환해서 보냄
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists()) // 기본 메시지 먼지
				.andExpect(jsonPath("$[0].code").exists()) // 코디가 있는지
//				.andExpect(jsonPath("$[0].rejectedValue").exists()) // 입력을 거절당한 값은 무엇인지 
//				.andExpect(jsonPath("$[0].field").exists()) // 어떤 필드에서 발생한 에러
				;
		
	}
}
