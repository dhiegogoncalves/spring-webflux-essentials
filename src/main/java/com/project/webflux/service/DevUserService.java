package com.project.webflux.service;

import com.project.webflux.domain.DevUser;
import com.project.webflux.repository.DevUserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class DevUserService implements ReactiveUserDetailsService {

  private final DevUserRepository devUserRepository;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return devUserRepository.findByUsername(username).cast(UserDetails.class);
  }

  public Mono<DevUser> findById(int id) {
    return devUserRepository.findById(id).switchIfEmpty(monoResponseStatusNotFoundException());
  }

  public Mono<DevUser> save(DevUser devUser) {
    devUser.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(devUser.getPassword()));
    return devUserRepository.save(devUser);
  }

  private <T> Mono<T> monoResponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

}
