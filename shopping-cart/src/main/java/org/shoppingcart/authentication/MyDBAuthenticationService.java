package org.shoppingcart.authentication;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.shoppingcart.dao.AccountDAO;
import org.shoppingcart.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyDBAuthenticationService implements UserDetailsService {
	
	private static final Logger LOGEVENT = Logger.getLogger(MyDBAuthenticationService.class);

	@Autowired
	private AccountDAO accountDAO;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountDAO.findAccount(username);

		if (account == null) {
			throw new UsernameNotFoundException("User" + username + "was not found in our records");
		}

		// EMPLOYEE,MANAGER,..
		String role = account.getUserRole();
		ArrayList<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();

		// ROLE_EMPLOYEE, ROLE_MANAGER
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
		grantList.add(authority);

		boolean enabled = account.isActive();
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		UserDetails userDetails = (UserDetails) new User(account.getUsername(), account.getPassword(), enabled,
				accountNonExpired, credentialsNonExpired, accountNonLocked, grantList);
		LOGEVENT.info("MyDBAuthenticationService -> loadUserByUsername");
		return userDetails;
	}
}