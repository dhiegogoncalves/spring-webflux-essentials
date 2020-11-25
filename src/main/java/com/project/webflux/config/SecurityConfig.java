package com.project.webflux.config;

import com.project.webflux.service.DevUserService;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
    return serverHttpSecurity.csrf().disable().authorizeExchange().pathMatchers(HttpMethod.POST, "/animes/**")
        .hasRole("ADMIN").pathMatchers(HttpMethod.PUT, "/animes/**").hasRole("ADMIN")
        .pathMatchers(HttpMethod.DELETE, "/animes/**").hasRole("ADMIN").pathMatchers(HttpMethod.GET, "/animes/**")
        .hasRole("USER").pathMatchers(HttpMethod.POST, "/users/**").permitAll().anyExchange().authenticated().and()
        .formLogin().and().httpBasic().and().build();
  }

  @Bean
  ReactiveAuthenticationManager authenticationManager(DevUserService devUserService) {
    return new UserDetailsRepositoryReactiveAuthenticationManager(devUserService);
  }

  /*
   * @Bean public MapReactiveUserDetailsService userDetailsService() {
   * PasswordEncoder passwordEncoder =
   * PasswordEncoderFactories.createDelegatingPasswordEncoder();
   * 
   * UserDetails admin =
   * User.withUsername("admin").password(passwordEncoder.encode("123456")).roles(
   * "ADMIN", "USER") .build(); UserDetails user =
   * User.withUsername("user").password(passwordEncoder.encode("123456")).roles(
   * "USER").build();
   * 
   * return new MapReactiveUserDetailsService(admin, user); }
   */
}
