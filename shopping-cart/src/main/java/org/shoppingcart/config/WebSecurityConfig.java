package org.shoppingcart.config;

import org.apache.log4j.Logger;
import org.shoppingcart.authentication.MyDBAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
// @EnableWebSecurity = @EnableWebMvcSecurity + Extra features
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGEVENT = Logger.getLogger(SpringWebAppInitializerConfig.class);

	@Autowired
	MyDBAuthenticationService myDBAuthenticationService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		LOGEVENT.info("WebSecurityConfig -> passwordEncoder");
		return encoder;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// For User in database
		auth.userDetailsService(myDBAuthenticationService).passwordEncoder(passwordEncoder());
		LOGEVENT.info("WebSecurityConfig -> configureGlobal");
	}

	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable();

		// The pages requires login as EMPLOYEE or MANAGER.
		// If no login, it will redirect to /login page.
		httpSecurity.authorizeRequests().antMatchers("/orderList", "/order", "accountInfo")
				.access("hasAnyRole('ROLE_EMPLOYEE','ROLE_MANAGER')");

		// For MANAGER only
		httpSecurity.authorizeRequests().antMatchers("/product").access("hasRole('ROLE_MANAGER')");

		// When the user has logged in as XX.
		// But access a page that requires role YY,
		// AccessDeniedException will throw.
		httpSecurity.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

		// Configure for Login Form
		httpSecurity.authorizeRequests().and().formLogin()
				// Submit URL of login page.
				.loginProcessingUrl("/j_spring_security_check")
				// Submit URL
				.loginPage("/login")//
				.defaultSuccessUrl("/accountInfo")
				.failureUrl("/login?error=true")
				.usernameParameter("username")
				.passwordParameter("password")
				// Config for Logout Page
				// (Go to home page).
				.and().logout().logoutUrl("/logout").logoutSuccessUrl("/");

		LOGEVENT.info("WebSecurityConfig -> configure");
	}
}