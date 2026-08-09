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
		        ).permitAll()   // Public: no token required

		        // Any authenticated company member can view their own company / own user profile
		        .requestMatchers(HttpMethod.GET, "/companies/me", "/users")
		        .hasAnyAuthority("MASTER_ADMIN", "ADMIN", "MANAGER", "EMPLOYEE")

		        // Any authenticated company member can edit their own user profile (service scopes to self when userId omitted)
		        .requestMatchers(HttpMethod.PUT, "/users")
		        .hasAnyAuthority("MASTER_ADMIN", "ADMIN", "MANAGER", "EMPLOYEE")

		        // User administration (list, create, delete other users) — ADMIN manages users within their company
		        .requestMatchers("/users/**")
		        .hasAnyAuthority("MASTER_ADMIN", "ADMIN")

		        // Platform-level tenant management (create/list/update/delete companies) — MASTER_ADMIN only
		        .requestMatchers("/companies/**")
		        .hasAuthority("MASTER_ADMIN")

		        // Approval matrix configuration (who approves what, at what amount) — company-specific admin duty
		        .requestMatchers("/approval-matrices/**")
		        .hasAnyAuthority("MASTER_ADMIN", "ADMIN")
		        
		        // View approval history (audit trail) of a procurement case.
		        .requestMatchers(HttpMethod.GET, "/approvals/history/**")
		        .hasAnyAuthority("MASTER_ADMIN", "ADMIN", "MANAGER", "EMPLOYEE")

		        // Approve/reject a pending case — only the assigned approver's role can act (service double-checks identity)
		        .requestMatchers(HttpMethod.PATCH, "/approvals/*/approve", "/approvals/*/reject")
		        .hasAnyAuthority("MASTER_ADMIN", "MANAGER")

		        // View own pending-approval inbox — reviewers only
		        .requestMatchers(HttpMethod.GET, "/approvals/pending-approval/**")
		        .hasAnyAuthority("MASTER_ADMIN", "MANAGER")

		        // Submit a case for approval — only the case owner (Procurement Executive) does this
		        .requestMatchers(HttpMethod.PATCH, "/approvals/*/submit")
		        .hasAnyAuthority("MASTER_ADMIN", "EMPLOYEE")

		        // Read access to procurement cases — Procurement Executives (own cases, enforced in service) and Managers reviewing them
		        .requestMatchers(HttpMethod.GET, "/procurement-cases/**")
		        .hasAnyAuthority("MASTER_ADMIN", "ADMIN", "MANAGER", "EMPLOYEE")

		        // Create/edit/delete procurement cases — Procurement Executive owns the case lifecycle while it's a draft
		        .requestMatchers("/procurement-cases/**")
		        .hasAnyAuthority("MASTER_ADMIN", "EMPLOYEE")

		        // Read access to vendor quotes — Executives managing quotes and Managers reviewing them during approval
		        .requestMatchers(HttpMethod.GET, "/vendor-quotes/**")
		        .hasAnyAuthority("MASTER_ADMIN", "ADMIN", "MANAGER", "EMPLOYEE")

		        // Create/edit/delete vendor quotes — only the case owner, only while the case is still a draft (enforced in service)
		        .requestMatchers("/vendor-quotes/**")
		        .hasAnyAuthority("MASTER_ADMIN", "EMPLOYEE")

		        // Fallback: anything not explicitly matched above just needs a valid, authenticated session
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


