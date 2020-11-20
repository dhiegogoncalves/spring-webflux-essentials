package com.project.webflux.controller;

import com.project.webflux.domain.Anime;
import com.project.webflux.service.AnimeService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("animes")
@RequiredArgsConstructor
public class AnimeController {

  private final AnimeService animeService;

  @GetMapping
  public Flux<Anime> listAll() {
    return animeService.findAll();
  }

  @GetMapping("{id}")
  public Mono<Anime> findById(@PathVariable int id) {
    return animeService.findById(id).switchIfEmpty(monoResponseStatusNotFoundException());
  }

  public <T> Mono<T> monoResponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
  }
}
