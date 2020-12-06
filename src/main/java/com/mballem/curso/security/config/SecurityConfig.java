package com.mballem.curso.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mballem.curso.security.service.UsuarioService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	UsuarioService usuarioService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		// acesso publico liberado.
		.antMatchers("/webjars/**", "/css/**", "/images/**", "/js/**").permitAll()
		.antMatchers("/", "/home").permitAll()
		// acessos privados admin
		.antMatchers("/u/**").hasAuthority("ADMIN")
		// acessos privados admin
		.antMatchers("/medicos/**").hasAuthority("MEDICO")
		
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
			.logoutSuccessUrl("/")
			
		.and()
			.exceptionHandling()
			.accessDeniedPage("/acesso-negado");
		
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(usuarioService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	
	
	

}
