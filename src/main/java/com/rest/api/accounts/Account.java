package com.rest.api.accounts;

import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Builder 어노테이션을 사용하면 자동으로 해당 클래스에 빌더를 추가해주기 때문에 매우 편리합니다.
 * @RequiredArgsConstructor : final이나 @NonNull 인 필드 값만 파라미터로 받는 생성자를 만들어 준다.
 * @AllArgsConstructor : 모든 필드 값을 파라미터로 받는 생성자를 만들어 준다.
 * @NorgsConstructor : 파라미터가 없는 기본 생성자 생성
 * @author lee
 * 
 */
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

	@Id @GeneratedValue 
	private Integer id;
	
	private String email;
	
	private String password;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Set<AccountRole> role;
}
