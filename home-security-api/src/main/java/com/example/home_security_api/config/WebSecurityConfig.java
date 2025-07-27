package com.example.home_security_api.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private AppConfig appConfig;

	public WebSecurityConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
			.cors(Customizer.withDefaults())
			.csrf(cors -> cors.disable())
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/").permitAll()
				.anyRequest().authenticated()
			)
            .formLogin(form -> form
				.defaultSuccessUrl(appConfig.getAddressAndPort() + "/files", true)
			)
			.logout(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(appConfig.getAddressAndPort()));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","DELETE","OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
	return source;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user =
			User.withUsername(appConfig.getUser().getUsername())
			 	.password("{bcrypt}" + passwordEncoder(appConfig.getUser().getPassword()))
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	public String passwordEncoder(String password) {
		return new BCryptPasswordEncoder().encode(password);
	}
}
