package com.rest.api.accounts;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

	@Autowired
	AccountRepository accountRepository;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Account account = accountRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException(userName)); //유저해당하는 데이터가 없으면 에러던짐
	
		
		// 스프링 시큐리티가 이해할 수있느 UserDetails객체로 변환 해야댐
		// UserDetails에 보면 User 라는 타입이 있다. role을 authorities로 변환해야댐.
		return new User(account.getEmail(), account.getPassword(), authorities(account.getRole()));
	}
	//  role을 authorities로 변환해야댐.
	private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
		return roles.stream()
				.map(r -> new SimpleGrantedAuthority("ROLE" + r.name()))
				.collect(Collectors.toSet());
	}
	
}
