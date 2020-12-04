package com.mballem.curso.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		// acesso publico liberado.
		.antMatchers("/webjars/**", "/css/**", "/images/**", "/js/**").permitAll()
		.antMatchers("/", "/home").permitAll()
		
		.anyRequest().authenticated()
		// configuracao da pagina de login e o acesso de usuario
		.and()
			.formLogin()
			.loginPage("/login")
			.defaultSuccessUrl("/", true)
			.failureUrl("/login-error")
			.permitAll()
		// configuracao da pagina de logout
		.and()
			.logout()
			.logoutSuccessUrl("/");
		
	}
	
	

}
