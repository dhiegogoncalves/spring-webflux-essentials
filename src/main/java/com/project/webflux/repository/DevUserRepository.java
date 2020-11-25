package com.project.webflux.repository;

import com.project.webflux.domain.DevUser;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface DevUserRepository extends ReactiveCrudRepository<DevUser, Integer> {

  Mono<DevUser> findByUsername(String username);
}
