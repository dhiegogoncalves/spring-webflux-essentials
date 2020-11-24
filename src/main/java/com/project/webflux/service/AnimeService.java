package com.project.webflux.service;

import java.util.List;

import com.project.webflux.domain.Anime;
import com.project.webflux.repository.AnimeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.netty.util.internal.StringUtil;
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
    return animeRepository.findById(id).switchIfEmpty(monoResponseStatusNotFoundException());
  }

  public Mono<Anime> save(Anime anime) {
    return animeRepository.save(anime);
  }

  public Mono<Void> update(Anime anime) {
    return findById(anime.getId()).flatMap(animeRepository::save).then();
  }

  public Mono<Void> delete(int id) {
    return findById(id).flatMap(animeRepository::delete);
  }

  @Transactional
  public Flux<Anime> saveAll(List<Anime> animes) {
    return animeRepository.saveAll(animes).doOnNext(this::throwResponseStatusExceptionWhenEmptyName);
  }

  private <T> Mono<T> monoResponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
  }

  private void throwResponseStatusExceptionWhenEmptyName(Anime anime) {
    if (StringUtil.isNullOrEmpty(anime.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Name");
    }
  }

}
