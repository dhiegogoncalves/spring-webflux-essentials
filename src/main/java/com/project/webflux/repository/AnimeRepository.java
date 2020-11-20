package com.project.webflux.repository;

import com.project.webflux.domain.Anime;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AnimeRepository extends ReactiveCrudRepository<Anime, Integer> {

}
