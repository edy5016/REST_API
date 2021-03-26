package com.rest.api;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.rest.api.event.Event;
class EventTest {

	@Test
	public void builder() {
		Event event = Event.builder()
				.name("test")
				.description("testDesc")
				.build();
		assertThat(event).isNotNull();
	}

	@Test
	public void javaBean() {
		Event event = new Event();
		String name = "Event";
		String description = "Spring";
		event.setName(name);
		event.setDescription(description);
		
		assertThat(event.getName()).isEqualTo(name);
		assertThat(event.getDescription()).isEqualTo(description);
	}
}