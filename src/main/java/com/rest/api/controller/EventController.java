package com.rest.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rest.api.common.ErrorsResource;
import com.rest.api.event.Event;
import com.rest.api.event.EventDto;
import com.rest.api.event.EventRepository;
import com.rest.api.event.EventResource;
import com.rest.api.event.EventValidator;

@Controller
//HAL JSON 이런 응답으로 응답을 보냄
@RequestMapping(value="/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

	private final EventRepository eventRepository;
	
	private final ModelMapper modelMapper;
	
	private final EventValidator eventValidator;
	
	public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}
	
	// @Valid는 request에 있는 값들을 바인딩할때 검증을 수행할 수 있다. 검증을 수행한 결과를 Errors 객체에 담아준다. 
	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
		if (errors.hasErrors()) {
//			return ResponseEntity.badRequest().body(errors);
			return badRequest(errors);
		}
		// Errors 객체는 자바빈 스팩을 준수하고 있는 객체가아니라 BeanSerialize 통해서 변환 못함 .
		// return ResponseEntity.badRequest().build().body(errors); 그러므로 블가능
		eventValidator.validate(eventDto, errors);
		if (errors.hasErrors()) {
//			return ResponseEntity.badRequest().body(errors);
			return badRequest(errors);
		}
		
//		Event event =  Event.builder()
//				.name(eventDto.getName())
//				.description(eventDto.getDescription())
//				.build(); --> 이런과정을 해줘야 되는데  라이브러리 사용하면 간편하게 가능.
		Event event = modelMapper.map(eventDto, Event.class); // 이벤트 객체로 변환
		event.update();  //이벤트 저장하기 전에 이벤트를 갱신해서 유료인지 무료인지 변경해줘야댐. (원래서비스쪽으로 지금은 서비스 간단해서)
		
		Event newEvent = this.eventRepository.save(event);
		Link selfLinkBuilder = linkTo(EventController.class).withRel("query-events");
		// 링크를 만들고 uri로 변환. 
		URI createUri = selfLinkBuilder.toUri();
		//created 할떄는 url 이 필요
		
		// 이벤트 객체를 이벤스 리소스로 변환. 변환하면 링크를 추가 할 수 있다.
		EventResource eventResource = new EventResource(newEvent);
		eventResource.add(linkTo(EventController.class).withRel("query-events")); //query-events 링크를 만듬. 
		// eventResource.add(selfLinkBuilder.withSelfRel()); // withSelfRel()를 해주면 self링크를 만들어줌. self 링크가 location header랑 같음.
		// eventResource 만들떄 self 링크는 만들어 줘서 주석처리.
		eventResource.add(selfLinkBuilder.withRel("update-event")); // 릴레이션은 다른데 링크는 같음. 
		eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
		
		
		/**
		 *  보통 self-link는 해당 이벤트 리소스 마다 설정해야되니깐  이벤트 리소스에 추가해주는 게 좋음.
		 */
		
		return ResponseEntity.created(createUri).body(eventResource); // 이벤트 리소스를 본문에 넣어줌.
	}

	/**
	 * 
	 * Pageable : 이 인터페이스는 페이징과 관련된 파라미터들을 받아올 수 있음.
	 * 페이지를 리소스로 바꿔서 링크 정보(현재페이지, 다음페이지, 마지막페이지..) 를 만들어야 되는데 그떄 유용하게 사용하는게 JPA가 제공하는 PagedResourcesAssembler 사용
	 * PagedResourcesAssembler를 사용해서 리소스로 변경
	 * 
	 * 한 건에대한 링크는 없다.
	 * 각각에 들어있는 것을 이벤트 리소르로 변경함 
	 *   assembler.toModel(page) -> assembler.toModel(page, e -> new EventResource(e));
	 */
	@GetMapping
	public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		RepresentationModel pageResources = assembler.toModel(page, e -> new EventResource(e));
		pageResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile")); // 링크 추가
		return ResponseEntity.ok(pageResources);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id); // 아이디를 찾아옴 찾아오면 기본타입이 Optional
		if (optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build(); 
		}
		// 꺼내서 리소르로 변경해서 보냄
		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));  //#resources-events-list이런 정보들은 apidoc에서 정의되있음
		return ResponseEntity.ok(eventResource);
		
	}
	
	private ResponseEntity badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors)); //bad request 만들떄
 	}
	
   
}
