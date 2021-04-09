package com.rest.api;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.common.RestDocsConfiguration;
import com.rest.api.common.TestDescription;
import com.rest.api.event.Event;
import com.rest.api.event.EventDto;
import com.rest.api.event.EventRepository;
import com.rest.api.event.EventStatus;


//@WebMvcTest // 웹 과 관련된 빈들이 모두 등록 됨.  웹용 빌드들만 빈에등록해줌
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc  // @SpringBootTest 사용시 mocmvc 쓸려면 이거 써야됨/
@AutoConfigureRestDocs // restdocs 사용하기 위한 어노테이션
@Import(RestDocsConfiguration.class) //다른 스프링 빈설정을 읽어와서 사용하는 방법
@ActiveProfiles("test") //test 프로파일로 프로파일로 실행하겠다. 기본적인 application.properties랑 test application.properties 사용하게된다. 
public class EventControllerTests {

	// moc mvc 주입 받아서 사용가능
	@Autowired
	MockMvc mockMvc; // 가짜 요청을 만들어서 디스패처 서블릿을 만들어서 요청 및 응답 받을 수 있다. 웹서버를 만들지않아서 빠름. repository를 등록해주지않음.
	
	@Autowired
	ObjectMapper objectMapper;
	
//	@MockBean //repository에 해당하는 빈을 목으로 만들어 달라.
//	EventRepository eventRepository;
	
	@Autowired
	EventRepository eventRepository;
	

	@Test
	@TestDescription("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {
		EventDto event = EventDto.builder()
				.name("String")
				.description("REST API")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14, 4, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 13, 14, 4, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 10, 14, 4, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 15, 15, 4, 21))
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
				.accept(MediaTypes.HAL_JSON_VALUE)  //내가원하는 요청은 HAL 응답을 받고싶다.
				.content(objectMapper.writeValueAsString(event))) // objectMapper json 문자열로 바꿔서 요청본문에 요청
			  .andDo(print()) // 응답 확인 하기 위해
			  .andExpect(status().isCreated())
			  .andExpect(jsonPath("id").exists())
			  .andExpect(header().exists(HttpHeaders.LOCATION))
			  .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
			  .andExpect(jsonPath("free").value(false))
			  .andExpect(jsonPath("offline").value(true))
			  .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
			  .andExpect(jsonPath("_links.self").exists())  // 링크정보로 클라이언트는 링크정보를 보고 다음상태를 이동할 수 있어야 한다. 
			  .andExpect(jsonPath("_links.query-events").exists())
			  .andExpect(jsonPath("_links.update-event").exists())
			  .andDo(document("create-event",  //문서이름
					  links(
							  linkWithRel("self").description("link to self"),
							  linkWithRel("query-events").description("link to query events"),  
							  linkWithRel("update-event").description("link to update events"), 
							  linkWithRel("profile").description("profile to  events")
					  ),
					  requestHeaders(
							  headerWithName(HttpHeaders.ACCEPT).description("accept header"),
							  headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
					  ),
					  requestFields(
							  fieldWithPath("name").description("Name of new event"),
							  fieldWithPath("description").description("description of new event"),
							  fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
							  fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
							  fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
							  fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
							  fieldWithPath("location").description("location of new event"),
							  fieldWithPath("maxPrice").description("maxPrice of new event"),
							  fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
							  fieldWithPath("basePrice").description("basePrice of new event")
					  ),
					  responseHeaders(
							  headerWithName(HttpHeaders.LOCATION).description("location header"),
							  headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
			  		  ),
					  /**
					   *   relaxedResponseFields 응답의 부가적인 정보가 더있어도 무시함. relaxedResponseFields 단점은 정확한 문서를 만들지 못함.
					   *   장점 : 문서 일부분만 테스트 할수 있다.
					   *   단점 : 정확한 문서를 생성하지 못한다.
					   *   
					   *   responseHeaders : links 만들어준 부분을 본문에 일부로 보기때문에 검증을 안했기 때문에 relaxedReponseFields를 사용.
					   *   relaxedReponseFields 일부분만 확인하는데 우리가 기술한 필드만 일치만 하면 성공 
					   *   responseHeaders 사용시 에러남. 이미 links 정보를 이미 위에서 확인하고 문서화했다. 그런데 단지 응답에서 links가 없다고 에러난다.
					   *   선택지는 두가지다.
					   *   1. relaxedReponseFields
					   *   2. responseHeaders 사용시 링크스 필드를 체크
					   *   	  fieldWithPath("_links.self").description("link to self")
					   *   	  fieldWithPath("_links.query-events").description("link to query-events")
					   *   	  fieldWithPath("_links.update-event").description("link to update-event")
					   *      fieldWithPath("_links.profile").description("profile to update-event")
					   */
					  relaxedResponseFields(  
							  fieldWithPath("id").description("Nadme of new event"),
							  fieldWithPath("name").description("Name of new event"),
							  fieldWithPath("description").description("description of new event"),
							  fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
							  fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
							  fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
							  fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
							  fieldWithPath("location").description("location of new event"),
							  fieldWithPath("maxPrice").description("maxPrice of new event"),
							  fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
							  fieldWithPath("basePrice").description("basePrice of new event"),
							  fieldWithPath("free").description("free of new event"),
							  fieldWithPath("offline").description("offline of new event"),
							  fieldWithPath("eventStatus").description("eventStatus of new event")
					  )
			   ))
			  ;
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
//	
//	@Test
//	@TestDescription("입력 받을 수 없는 값이 비어 있는 경우 발생하는 테스트")
//	public void createEventBadRequestEmptyInput() throws Exception {
//		EventDto eventDto = EventDto.builder().build();
//		
//		this.mockMvc.perform(post("/api/events")
//				.contentType(MediaType.APPLICATION_JSON) // json 보내는 content 
//				.accept(this.objectMapper.writeValueAsString(eventDto)))  // object로변환해서 보냄
//				.andExpect(status().isBadRequest());
//	}
	
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
				.andExpect(jsonPath("_links.index").exists()) // 인덱스 링크가 있기를 기대
//				.andExpect(jsonPath("$[0].rejectedValue").exists()) // 입력을 거절당한 값은 무엇인지 
//				.andExpect(jsonPath("$[0].field").exists()) // 어떤 필드에서 발생한 에러
				;
		
	}
	
	@Test
	@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvents() throws Exception {
		
//		IntStream.range(0, 30).forEach(i -> {
//			this.generationEvent(i);
//		});
		
		// Given 
		IntStream.range(0, 30).forEach(this::generationEvent);
		
		//when 
		this.mockMvc.perform(get("/api/events")
				.param("page", "1")
				.param("size", "10")
				.param("sort", "name,DESC")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("page").exists()) //페이지 관련된 파라미터
			.andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.profile").exists()) //profile 우리가 만들어야뎀 
			.andDo(document("query-events")) //profile을 추가할라면 document를 만들어야됨
			// todo  links first 는 처음 페이지다 
			// links next 는 다음페이지다 문서화 필요
			;
	}
	
	// 이벤트 30 개 저장
	private void generationEvent(int index) {
		Event event = Event.builder()
				.name("event" +index)	
				.description("test event")
				.build();
		
		this.eventRepository.save(event);
	}
}
