package com.example.bankapplication.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.cors(AbstractHttpConfigurer::disable)
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/admin/**").hasAuthority("ADMIN")
						.requestMatchers("/user/**").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers("/qr/**").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers("/secured").hasAuthority("USER")
						.requestMatchers("/currency").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers("/loan/**").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers("/test/**").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers("/generate/**").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers("/transaction/user").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers("/transferMoney").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers("/home", "/profile").hasAnyAuthority("USER", "ADMIN")
						.anyRequest()
						.authenticated()
				)
				.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}
}
