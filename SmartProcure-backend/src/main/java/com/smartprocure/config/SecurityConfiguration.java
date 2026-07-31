package com.smartprocure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.smartprocure.security.CustomJwtVerificationFilter;
import com.smartprocure.security.JwtAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	
	private final CustomJwtVerificationFilter customJWTVerificationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Bean
	SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception
	{
		
		http.csrf(csrf -> csrf.disable());
		
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.exceptionHandling(exception-> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint));
		
		http.authorizeHttpRequests(request ->
		
		request.requestMatchers(
		        "/auth/login",
		        "/swagger-ui/**",
		        "/v3/api-docs/**"
				).permitAll()
		
				.requestMatchers(HttpMethod.GET, "/companies/me", "/users")
				.hasAnyAuthority("ADMIN", "MANAGER", "EMPLOYEE")
		
				.requestMatchers("/companies/**")
				.hasAuthority("MASTER_ADMIN")
		
				.requestMatchers(HttpMethod.PUT, "/users")
				.hasAnyAuthority("ADMIN", "MANAGER", "EMPLOYEE")
		
				.requestMatchers("/users/**")
				.hasAuthority("ADMIN")
				
				.requestMatchers(HttpMethod.GET, "/procurement-cases/**")
				.hasAnyAuthority("MANAGER","EMPLOYEE")
				
				.requestMatchers("/procurement-cases/**")
				.hasAuthority("EMPLOYEE")
				
				
		
				.anyRequest().authenticated());
		
		http.addFilterBefore(customJWTVerificationFilter, UsernamePasswordAuthenticationFilter.class);
	
		return http.build();
		
	}
	
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
	{
		return config.getAuthenticationManager();
	}


}


