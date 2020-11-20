package com.project.webflux.service;

import com.project.webflux.domain.Anime;
import com.project.webflux.repository.AnimeRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnimeService {

  private final AnimeRepository animeRepository;

  public Flux<Anime> findAll() {
    return animeRepository.findAll();
  }

  public Mono<Anime> findById(int id) {
    return animeRepository.findById(id);
  }
}
