package com.project.webflux.controller;

import com.project.webflux.domain.Anime;
import com.project.webflux.repository.AnimeRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("animes")
@RequiredArgsConstructor
public class AnimeController {

  private final AnimeRepository animeRepository;

  @GetMapping
  public Flux<Anime> listAll() {
    return animeRepository.findAll();
  }
}
