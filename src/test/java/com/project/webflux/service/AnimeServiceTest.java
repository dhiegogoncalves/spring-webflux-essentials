package com.project.webflux.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.project.webflux.domain.Anime;
import com.project.webflux.repository.AnimeRepository;
import com.project.webflux.util.AnimeCreator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

  @InjectMocks
  private AnimeService animeService;

  @Mock
  private AnimeRepository animeRepository;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(animeRepository.findAll()).thenReturn(Flux.just(anime));

    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepository.save(AnimeCreator.createAnimeToBeSaved())).thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepository.delete(ArgumentMatchers.any(Anime.class))).thenReturn(Mono.empty());
  }

  @Test
  public void BlockHoundWorks() {
    try {
      FutureTask<?> task = new FutureTask<>(() -> {
        Thread.sleep(0);
        return "";
      });

      Schedulers.parallel().schedule(task);

      task.get(10, TimeUnit.SECONDS);
      Assertions.fail("should fail");
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof BlockingOperationError);
    }
  }

  @Test
  @DisplayName("findAll returns a Flux of anime")
  public void findAll_ReturnFluxOfAnime_WhenSuccessful() {
    StepVerifier.create(animeService.findAll()).expectSubscription().expectNext(anime).verifyComplete();
  }

  @Test
  @DisplayName("findById returns Mono error when anime does not exist")
  public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

    StepVerifier.create(animeService.findById(1)).expectSubscription().expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("save creates an anime when successful")
  public void save_CreatesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeService.save(animeToBeSaved)).expectSubscription().expectNext(anime).verifyComplete();
  }

  @Test
  @DisplayName("update save updated anime and returns empty Mono when successful")
  public void update_SaveUpdatedAnime_WhenSuccessful() {
    BDDMockito.when(animeRepository.save(AnimeCreator.createValidAnime())).thenReturn(Mono.empty());

    StepVerifier.create(animeService.update(AnimeCreator.createValidAnime())).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("update returns Mono error when anime does exist")
  public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

    StepVerifier.create(animeService.update(AnimeCreator.createValidAnime())).expectSubscription()
        .expectError(ResponseStatusException.class).verify();
  }

  @Test
  @DisplayName("delete returns Mono error when anime does not exist")
  public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

    StepVerifier.create(animeService.delete(1)).expectSubscription().expectError(ResponseStatusException.class)
        .verify();
  }
}
