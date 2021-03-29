package com.rest.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.rest.api.event.Event;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

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
	
//	@Test
//	@Parameters({
//		"0, 0, true",
//		"100, 0, false",
//		"0, 100, false"
//	})
//	public void testFree(int basePrice, int maxPrice, boolean isFree) {
//		// Given
//		Event event = Event.builder()
//				.basePrice(basePrice)
//				.maxPrice(maxPrice)
//				.build();
//		//when
//		event.update();
//		
//		//then
//		assertThat(event.isFree()).isEqualTo(isFree);
//	}
	
	@Test
	@Parameters(method = "parametersForTestFree")
	public void testFree(int basePrice, int maxPrice, boolean isFree) {
		// Given
		Event event = Event.builder()
				.basePrice(basePrice)
				.maxPrice(maxPrice)
				.build();
		//when
		event.update();
		
		//then
		assertThat(event.isFree()).isEqualTo(isFree);
	}
	
	private Object[] parametersForTestFree() {
		return new Object[] {
			new Object[] { 0, 0, true},
			new Object[] {100, 0, false},
			new Object[] {0, 100, false},
			new Object[] {100, 200, false}
		};
	}
	
	
	@Test
	@Parameters
	public void testOffline(String location, boolean isOffline) {
		// Given
		Event event = Event.builder()
				.location(location)
				.build();
		//when
		event.update();
		
		//then
		assertThat(event.isOffline()).isEqualTo(isOffline);
		
//		// Given
//		event = Event.builder()
//				.build();
//		//when
//		event.update();
//		
//		//then
//		assertThat(event.isOffline()).isFalse();
	}
	
	private Object[] parametersForTestOffline() {
		return new Object[] {
				new Object[] {"강남", true},
				new Object[] {null, false},
				new Object[] { " ", false}
		};
	}
}
