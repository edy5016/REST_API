package com.rest.api.event;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.rest.api.controller.EventController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

// 객체를 json으로 변환하는 것은 serializer (@BeanSerializer)
public class EventResource extends RepresentationModel {

	// BeanSerializer는 기본적으로 필드이름을 사용함(여기선 컴포즈 객체 event)
	// 이것을 꺼내고 싶으면 (event 밖으로 꺼내고 싶을때 
	/**  
	 *  방법 1.필드들만 복사해서 리소스에다 쭉 붙여 넣는 방법 
	 *  public EventResource(Event event) {	
			this.name = name; 
			...
	   }
	   
	   
	 *  방법 2. @JsonUnwrapped ㅣ 이벤트가 감싸져 있지 않음.
	 *    이렇게 하면 코드량이 느니까 다른 방법도 있다.
	 *    
	 *    
	 *  방법 3.  extends Resource
	 *  public class EventResource extends Resource<Event> {
	 *  	public EventResource(Event content, Link... links) {
	 *  		super(content, links);	 
	 *      }
	 *  
	 *  }
	 * */ 
	@JsonUnwrapped
	private Event event;
	
	public EventResource(Event event) {
		this.event = event;
		
		/**
		 *  보통 self-link는 해당 이벤트 리소스 마다 설정해야되니깐  이벤트 리소스에 추가해주는 게 좋음.
		 *  add(new Link("http://localhost:8080/api/events/" + event.getId())) 이거랑 같은 것 
		 */
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}
	
	
	public Event getEvent() {
		return event;
	}
}
