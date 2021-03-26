package com.rest.api.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// 테스트 코드에서 다읽기도 머하고 해서 description 용  어노테이션 만듬.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE) // 이 에노테이션을 붙인 것을 얼마나 오래 가지 갈 것인가임.
public @interface TestDescription {
	String value();
}
