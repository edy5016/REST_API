package com.rest.api.common;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

// Errrors 를 변환할 JsonSerializer
// ErrorsSerializer 를 우리가 사용하는 ObjectMapper에 등록을 해야됨. (@JsonComponent 사용) 
@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors>{

	@Override
	public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartArray();
		// erorrs 안에는 에러가 여러개니까 이것을 배열로 담아주기위해 writeStartArray,writeEndArray 사용
		
//		이경우 errors.reject("wrongPrices", "Value of prices are wrong"); 글로벌 에러 , errors.rejectValue() 이경우 필드에러
		// 필드에러 각각에러마다 Object 를 만듬
		errors.getFieldErrors().forEach(e -> {
			try {
				gen.writeStartObject();
				gen.writeStringField("fieldName", e.getField()); // 어떤 필드에 해당하는지  
				gen.writeStringField("objectName", e.getObjectName()); // object name 은 무엇인지
				gen.writeStringField("code", e.getCode()); // 코드는 무엇인지   이런식으로 json object를 채움.
				gen.writeStringField("defaultMessage", e.getDefaultMessage()); 
				Object rejectValue = e.getRejectedValue(); 
				if (rejectValue != null) { // rejectValue 있을 수 도 있고 없을 수도 있으니깐.
					gen.writeStringField("rejectedValue", rejectValue.toString());
				}
				gen.writeEndObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		// 글로벌 에러 : 필드 에러는 없다
		errors.getGlobalErrors().forEach(e -> {
			try {
	 			gen.writeStartObject();
				gen.writeStringField("objectName", e.getObjectName());
				gen.writeStringField("code", e.getCode());
				gen.writeStringField("defaultMessage", e.getDefaultMessage());
				gen.writeEndObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		gen.writeEndArray();
	}

}
