package com.project.webflux.controller;

import java.util.List;

import javax.validation.Valid;

import com.project.webflux.domain.Anime;
import com.project.webflux.service.AnimeService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("animes")
@RequiredArgsConstructor
public class AnimeController {

  private final AnimeService animeService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  public Flux<Anime> listAll() {
    return animeService.findAll();
  }

  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Anime> findById(@PathVariable int id) {
    return animeService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Anime> save(@Valid @RequestBody Anime anime) {
    return animeService.save(anime);
  }

  @PostMapping("batch")
  @ResponseStatus(HttpStatus.CREATED)
  public Flux<Anime> saveBatch(@RequestBody List<Anime> animes) {
    return animeService.saveAll(animes);
  }

  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> update(@PathVariable int id, @Valid @RequestBody Anime anime) {
    return animeService.update(anime.withId(id));
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable int id) {
    return animeService.delete(id);
  }
}