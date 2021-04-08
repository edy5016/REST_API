package com.rest.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController	
public class IndexController {
	
	// 루트가 나오길 바람. api로 요청했을떄 이핸들러가 적절하게 201응답으로 링크정보를 보내줌.
	@GetMapping("/api") 
	public RepresentationModel index() {
		RepresentationModel index = new RepresentationModel();
		index.add(linkTo(EventController.class).withRel("events"));
		return index;
	}
}
