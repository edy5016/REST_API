package com.rest.api.common;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.validation.Errors;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import com.rest.api.controller.IndexController;

public class ErrorsResource extends RepresentationModel {

	public ErrorsResource(Errors content, Link... links) {
		super();
		add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
	}
}

