package com.motive.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.motive.rest.user.*;

@Configuration
@EnableWebSecurity // tells Spring Boot to drop its auto configured security policy and use this one instead. For quick demos, autoconfigured security is okay. But for anything real, you should write the policy yourself.
@EnableGlobalMethodSecurity(prePostEnabled = true) // turns on method-level security with Spring Security’s sophisticated
public class SecurityPolicy extends WebSecurityConfigurerAdapter{

	@Autowired
	private CustomUserDetailsService userDetailsService;

    @Value("${JWT_SIGNATURE}")
    String JWTSignature;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(this.userDetailsService)
				.passwordEncoder(UserService.PASSWORD_ENCODER);
	}

	// use function to create filter so we can load the signature value
	private JWTAuthorizationFilter getFilter(String signature){
		return new JWTAuthorizationFilter(signature);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception { 
		http.cors().and().csrf().disable() //TODO
		.addFilterAfter(getFilter(JWTSignature), UsernamePasswordAuthenticationFilter.class)
		.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/user/login").permitAll()
		.antMatchers(HttpMethod.POST, "/user/register").permitAll()
		.anyRequest().authenticated();
	}


}
