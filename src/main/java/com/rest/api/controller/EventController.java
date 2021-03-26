package com.rest.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rest.api.event.Event;
import com.rest.api.event.EventDto;
import com.rest.api.event.EventRepository;
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
			return ResponseEntity.badRequest().body(errors);
		}
		// Errors 객체는 자바빈 스팩을 준수하고 있는 객체가아니라 BeanSerialize 통해서 변환 못함 .
		// return ResponseEntity.badRequest().build().body(errors); 그러므로 블가능
		eventValidator.validate(eventDto, errors);
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		
//		Event event =  Event.builder()
//				.name(eventDto.getName())
//				.description(eventDto.getDescription())
//				.build(); --> 이런과정을 해줘야 되는데  라이브러리 사용하면 간편하게 가능.
		Event event = modelMapper.map(eventDto, Event.class); // 이벤트 객체로 변환
		
		Event newEvent = this.eventRepository.save(event);
		// 링크를 만들고 uri로 변환. 
		URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		//created 할떄는 url 이 필요
		return ResponseEntity.created(createUri).body(event);
	}

}
