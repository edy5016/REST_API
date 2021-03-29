package com.rest.api.event;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(of = "id") @Builder @NoArgsConstructor @AllArgsConstructor 
@Entity
public class Event {

	@Id @GeneratedValue
	private Integer id;
	private String name;
	private String description;
	private LocalDateTime beginEnrollmentDateTime;
	private LocalDateTime closeEnrollmentDateTime;
	private LocalDateTime beginEventDateTime;
	private LocalDateTime endEventDateTime;
	private String location; // (optional) 이게 없으면 온라인 모임 private int basePrice; // (optional) private int
								// maxPrice; // (optional) private int limitOfEnrollment;
	
	private boolean offline;
	private boolean free;

	private int maxPrice;
	private int limitOfEnrollment;
	private int basePrice;
	@Enumerated(EnumType.STRING) // 기본값은 순서대로 0, 1, 2 로 하는데 나중에 값바뀌면 꼬일수도 있어서 String 으로 바꾸는게 나음.
	private EventStatus eventStatus = EventStatus.DRAFT;

	public void update() {
		if (this.basePrice == 0 && this.maxPrice == 0) {
			this.free = true;
		} else {
			this.free = false;
		}
		
		// update offline
		// isBlank 자바 11 부터 지원 (비어있는지 없는지 체크) 공백문자 까지 다 체크해줌.
		// 자바 11 밑은  String 클래스에있는 걸로 trim을 한 후 isEmpty() 사용 
		if (this.location == null || this.location.isBlank()) {
			this.offline = false;
		} else {
			this.offline = true;
		}
		
	}
}
