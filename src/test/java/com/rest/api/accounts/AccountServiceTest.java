package com.rest.api.accounts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

	// 비어있는 exception 으로 등록
	// expected 기떄문에 예상되는 예외를 먼저 적어줘야 된다.
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Test
	public void findByUserName() {
		//Given
		String password = "lee";
		String userName = "lee@naver.com";
		Account account = Account.builder()
				.email(userName)
				.password(password)
				.role(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		this.accountRepository.save(account);
		
		//when
		UserDetailsService userDetailsService = accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(userName); // unserName 읽어올 수 있는 지 확인.
		
		//then
		assertThat(userDetails.getPassword()).isEqualTo(password);
	}
	
	// username을 불러올래다가 실패
	@Test
	public void findByUsernameFail() {
		String username = "random@naver.com";
		try {
			fail("supposed to be failed");
		} catch(UsernameNotFoundException e) {
			assertThat(e.getMessage()).containsSequence(username);
		}
	}
	
	// username을 불러올래다가 실패
	@Test
	public void findByUsernameFailExpected() {
		// Expected 
		// 예측한것과 다르면 테스트가 실패 
		// expected 는 예측되는 예외를  먼저 적어줘야 된다.
		String username = "random@naver.com";
		expectedException.expect(UsernameNotFoundException.class);
		expectedException.expectMessage(Matchers.containsString(username));

		//when
		accountService.loadUserByUsername(username);
	}
}
