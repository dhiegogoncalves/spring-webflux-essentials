package com.project.webflux.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.project.webflux.domain.Anime;
import com.project.webflux.service.AnimeService;
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

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

  @InjectMocks
  private AnimeController animeController;

  @Mock
  private AnimeService animeServiceMock;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(animeServiceMock.findAll()).thenReturn(Flux.just(anime));

    BDDMockito.when(animeServiceMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));

    BDDMockito.when(animeServiceMock.save(AnimeCreator.createAnimeToBeSaved())).thenReturn(Mono.just(anime));

    BDDMockito.when(animeServiceMock.update(AnimeCreator.createValidAnime())).thenReturn(Mono.empty());

    BDDMockito.when(animeServiceMock.delete(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());
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
    StepVerifier.create(animeController.listAll()).expectSubscription().expectNext(anime).verifyComplete();
  }

  @Test
  @DisplayName("findById returns a Mono with anime when it exists")
  public void findById_ReturnMonoAnime_WhenSuccessful() {
    StepVerifier.create(animeController.findById(1)).expectSubscription().expectNext(anime).verifyComplete();
  }

  @Test
  @DisplayName("save creates an anime when successful")
  public void save_CreatesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeController.save(animeToBeSaved)).expectSubscription().expectNext(anime).verifyComplete();
  }

  @Test
  @DisplayName("update save updated anime and returns empty Mono when successful")
  public void update_SaveUpdatedAnime_WhenSuccessful() {
    StepVerifier.create(animeController.update(1, AnimeCreator.createValidAnime())).expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes the anime when successful")
  public void delete_RemovesAnime_WhenSuccessful() {
    StepVerifier.create(animeController.delete(1)).expectSubscription().verifyComplete();
  }

}