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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
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


////@WebMvcTest // ??? ??? ????????? ????????? ?????? ?????? ???.  ?????? ???????????? ??????????????????
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc  // @SpringBootTest ????????? mocmvc ????????? ?????? ?????????/
//@AutoConfigureRestDocs // restdocs ???????????? ?????? ???????????????
//@Import(RestDocsConfiguration.class) //?????? ????????? ???????????? ???????????? ???????????? ??????
//@ActiveProfiles("test") //test ??????????????? ??????????????? ???????????????. ???????????? application.properties??? test application.properties ??????????????????. 
public class EventControllerTests extends BaseControllerTest{

	// moc mvc ?????? ????????? ????????????
//	@Autowired
//	MockMvc mockMvc; // ?????? ????????? ???????????? ???????????? ???????????? ???????????? ?????? ??? ?????? ?????? ??? ??????. ???????????? ?????????????????? ??????. repository??? ?????????????????????.

//	@Autowired
//	ObjectMapper objectMapper;
	
//	@MockBean //repository??? ???????????? ?????? ????????? ????????? ??????.
//	EventRepository eventRepository;
	
	@Autowired
	EventRepository eventRepository;
	
//	@Autowired
//	ModelMapper modelMapper;

	@Test
	@TestDescription("??????????????? ???????????? ???????????? ?????????")
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
				.location("??????")
				.build();
		
//		event.setId(10);
//		Mockito.when(eventRepository.save(event)).thenReturn(event); // eventRepository??? ????????? ?????? event ????????? return ??????.
		
		
	 // perform?????? ????????? ??????
		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON) // ????????? ????????? JSON??? ????????? ????????? ????????? ?????????
				.accept(MediaTypes.HAL_JSON_VALUE)  //??????????????? ????????? HAL ????????? ????????????.
				.content(objectMapper.writeValueAsString(event))) // objectMapper json ???????????? ????????? ??????????????? ??????
			  .andDo(print()) // ?????? ?????? ?????? ??????
			  .andExpect(status().isCreated())
			  .andExpect(jsonPath("id").exists())
			  .andExpect(header().exists(HttpHeaders.LOCATION))
			  .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
			  .andExpect(jsonPath("free").value(false))
			  .andExpect(jsonPath("offline").value(true))
			  .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
			  .andExpect(jsonPath("_links.self").exists())  // ??????????????? ?????????????????? ??????????????? ?????? ??????????????? ????????? ??? ????????? ??????. 
			  .andExpect(jsonPath("_links.query-events").exists())
			  .andExpect(jsonPath("_links.update-event").exists())
			  .andDo(document("create-event",  //????????????
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
					   *   relaxedResponseFields ????????? ???????????? ????????? ???????????? ?????????. relaxedResponseFields ????????? ????????? ????????? ????????? ??????.
					   *   ?????? : ?????? ???????????? ????????? ?????? ??????.
					   *   ?????? : ????????? ????????? ???????????? ?????????.
					   *   
					   *   responseHeaders : links ???????????? ????????? ????????? ????????? ??????????????? ????????? ????????? ????????? relaxedReponseFields??? ??????.
					   *   relaxedReponseFields ???????????? ??????????????? ????????? ????????? ????????? ????????? ?????? ?????? 
					   *   responseHeaders ????????? ?????????. ?????? links ????????? ?????? ????????? ???????????? ???????????????. ????????? ?????? ???????????? links??? ????????? ????????????.
					   *   ???????????? ????????????.
					   *   1. relaxedReponseFields
					   *   2. responseHeaders ????????? ????????? ????????? ??????
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
	@TestDescription("?????? ?????? ??? ?????? ?????? ????????? ????????? ????????? ???????????? ?????????")
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
				.location("??????")
				.free(true)
				.offline(false)
				.eventStatus(EventStatus.PUBLISHED)
				.build();
		
//		event.setId(10);
//		Mockito.when(eventRepository.save(event)).thenReturn(event); // eventRepository??? ????????? ?????? event ????????? return ??????.
		
		
	 // perform?????? ????????? ??????
		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON) // ????????? ????????? JSON??? ????????? ????????? ????????? ?????????
				.accept(MediaTypes.HAL_JSON)  //??????????????? ????????? HAL ????????? ????????????.
				.content(objectMapper.writeValueAsString(event))) // objectMapper json ???????????? ????????? ??????????????? ??????
			  .andDo(print()) // ?????? ?????? ?????? ??????
			  .andExpect(status().isBadRequest()); // ?????????
	}
//	
//	@Test
//	@TestDescription("?????? ?????? ??? ?????? ?????? ?????? ?????? ?????? ???????????? ?????????")
//	public void createEventBadRequestEmptyInput() throws Exception {
//		EventDto eventDto = EventDto.builder().build();
//		
//		this.mockMvc.perform(post("/api/events")
//				.contentType(MediaType.APPLICATION_JSON) // json ????????? content 
//				.accept(this.objectMapper.writeValueAsString(eventDto)))  // object??????????????? ??????
//				.andExpect(status().isBadRequest());
//	}
	
	@Test
	@TestDescription("?????? ?????? ????????? ????????? ????????? ???????????? ?????????") //junit5 ?????? ???????????? ???????????? ??????????????? ????????? ???????????? ?????? junit??? ????????? ???????????? ??????.
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
				.location("??????")
				.build();
		
		this.mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON) // json ????????? content 
				.accept(MediaTypes.HAL_JSON_VALUE) 
				.content(this.objectMapper.writeValueAsString(event)))  // object??????????????? ??????
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists()) // ?????? ????????? ??????
				.andExpect(jsonPath("$[0].code").exists()) // ????????? ?????????
				.andExpect(jsonPath("_links.index").exists()) // ????????? ????????? ????????? ??????
//				.andExpect(jsonPath("$[0].rejectedValue").exists()) // ????????? ???????????? ?????? ???????????? 
//				.andExpect(jsonPath("$[0].field").exists()) // ?????? ???????????? ????????? ??????
				;
		
	}
	
	@Test
	@TestDescription("30?????? ???????????? 10?????? ????????? ????????? ????????????")
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
			.andExpect(jsonPath("page").exists()) //????????? ????????? ????????????
			.andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.profile").exists()) //profile ????????? ??????????????? 
			.andDo(document("query-events")) //profile??? ??????????????? document??? ???????????????
			// todo  links first ??? ?????? ???????????? 
			// links next ??? ?????????????????? ????????? ??????
			;
	}
	
	@Test
	@TestDescription("????????? ????????? ?????? ????????????")
	public void getEvent() throws Exception {
		//given
		Event event = this.generationEvent(100); // ????????? ????????????
		
		this.mockMvc.perform(get("/api/events/{id}", event.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				.andDo(document("get-an-event"))
		;
		
	}
	
	@Test
	@TestDescription("?????? ???????????? ???????????? ??? 404 ?????? ??????")
	public void getEvent404() throws Exception {
		// when & then
		this.mockMvc.perform(get("/api/events/11883"))
				.andExpect(status().isNotFound())
				;	
	}
	
	@Test
	@TestDescription("???????????? ??????????????? ????????????")
	public void updateEvent() throws Exception {
		// given
		Event event = this.generationEventUpdate(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		String eventName = "update Event";
		eventDto.setName(eventName);
		
		// when & then
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").value(eventName))
				.andExpect(jsonPath("_links.self").exists())
				.andDo(document("update-event")); // ?????????  links, request ??????, response ?????? ??????????????????
		;
	}
	
	@Test
	@TestDescription("???????????? ??????????????????  ????????? ?????? ??????")
	public void updateEventEmpty() throws Exception {
		Event event = this.generationEventUpdate(200);
		
		EventDto eventDto = new EventDto();
		
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto))
				)
			.andDo(print())
			.andExpect(status().isBadRequest());
			;
	}
	
	@Test
	@TestDescription("???????????? ????????? ????????? ????????? ?????? ??????")
	public void updateEventWrong() throws Exception {
		Event event = this.generationEventUpdate(200);
		
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);
		
		
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto))
				)
			.andDo(print())
			.andExpect(status().isBadRequest());
			;
	}
	
	@Test
	@TestDescription("???????????? ?????? ????????? ?????? ??????")
	public void updateEvent404() throws Exception {
		Event event = this.generationEventUpdate(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		
		
		this.mockMvc.perform(put("/api/events/1111111111")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto))
				)
			.andDo(print())
			.andExpect(status().isNotFound());
			;
	}
	
	// ????????? 30 ??? ??????
	private Event generationEvent(int index) {
		Event event = Event.builder()
				.name("event" +index)	
				.description("test event")
				.build();
		
		return this.eventRepository.save(event);
	}
	
	private Event generationEventUpdate(int index) {
		Event event = Event.builder()
				.name("event" + index)
				.description("REST API")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14, 4, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 13, 14, 4, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 10, 14, 4, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 15, 15, 4, 21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.free(false)
				.offline(true)
				.eventStatus(EventStatus.DRAFT)
				.location("??????")
				.build();
		
		return this.eventRepository.save(event);
	}			
}
