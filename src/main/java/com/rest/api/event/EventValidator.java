package com.rest.api.event;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

//@Component로 빈으로 등록해서 사용
@Component
public class EventValidator {
	
	// 발생한 에러들을 Errors 에 담아줄거임.
	public void validate(EventDto eventDto, Errors errors) {
		if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
//			errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
//			errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong.");
			errors.reject("wrongPrices", "Value of prices are wrong");
		}
		
		LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
		if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime())||
				endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
				endEventDateTime.isAfter(eventDto.getBeginEnrollmentDateTime())) {
			errors.rejectValue("endEventDateTime", "wrongVlaue", "endEventDateTime is wrong");
		}
		
		// TODO beginEventDateTime
		// TODO CloseEnrollmentDateTime
	}
}
