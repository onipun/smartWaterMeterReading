// https://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html

package com.example.psm.v1;

import java.util.List;

import com.example.psm.v1.model.PersonRepository;
import com.example.psm.v1.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/user/**","/login").permitAll()   
				.anyRequest().authenticated()
				.and()
			.formLogin()
			.loginProcessingUrl("/login")
			.successHandler(myAuthenticationSuccessHandler())
				.and()
			.httpBasic();
	}


	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.ldapAuthentication()
				.userDnPatterns("uid={0},ou=people")
				.groupSearchBase("ou=groups")
				.contextSource()
					.url("ldap://localhost:8389/dc=springframework,dc=org")
					.and()
				.passwordCompare()
					.passwordEncoder(new LdapShaPasswordEncoder())
						.passwordAttribute("userPassword");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
	  web
		.ignoring()
		   .antMatchers("/upload/**"); // #3
	}

	@Bean
	public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
        return new UrlaAuthenticationSuccessHandler();
	}

}