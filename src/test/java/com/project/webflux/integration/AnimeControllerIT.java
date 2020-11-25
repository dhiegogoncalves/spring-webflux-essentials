package com.project.webflux.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class AnimeControllerIT {

  @MockBean
  private AnimeRepository animeRepositoryMock;

  @Autowired
  private WebTestClient webTestClient;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(Flux.just(anime));

    BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved())).thenReturn(Mono.just(anime));

    BDDMockito
        .when(animeRepositoryMock
            .saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
        .thenReturn(Flux.just(anime, anime));

    BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class))).thenReturn(Mono.empty());
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
  @DisplayName("listAll returns a Flux of anime")
  public void listAll_ReturnFluxOfAnime_WhenSuccessful() {
    /*
     * webTestClient.get().uri("/animes").exchange().expectStatus().isOk().
     * expectBody().jsonPath("$.[0].id")
     * .isEqualTo(anime.getId()).jsonPath("$.[0].name").isEqualTo(anime.getName());
     */

    webTestClient.get().uri("/animes").exchange().expectStatus().isOk().expectBodyList(Anime.class).hasSize(1)
        .contains(anime);
  }

  @Test
  @DisplayName("findById returns a Mono with anime when it exists")
  public void findById_ReturnMonoAnime_WhenSuccessful() {
    webTestClient.get().uri("/animes/{id}", 1).exchange().expectStatus().isOk().expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("findById returns Mono error when anime does not exist")
  public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

    webTestClient.get().uri("/animes/{id}", 1).exchange().expectStatus().isNotFound().expectBody().jsonPath("$.status")
        .isEqualTo(404).jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
  }

  @Test
  @DisplayName("save creates an anime when successful")
  public void save_CreatesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    webTestClient.post().uri("/animes").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved)).exchange().expectStatus().isCreated().expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("save returns mono error with bad request when name is empty")
  public void save_ReturnsError_WhenNameIsEmpty() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");

    webTestClient.post().uri("/animes").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved)).exchange().expectStatus().isBadRequest().expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }

  @Test
  @DisplayName("saveBatch creates a list of anime when successful")
  public void saveBatch_CreatesListOfAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    webTestClient.post().uri("/animes/batch").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved))).exchange().expectStatus().isCreated()
        .expectBodyList(Anime.class).hasSize(2).contains(anime);
  }

  @Test
  @DisplayName("saveBatch returns Mono error when one of the objects in the list contains null or empty name")
  public void saveBatch_ReturnsMonoError_WhenContainsInvalidName() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    BDDMockito.when(animeRepositoryMock.saveAll(ArgumentMatchers.anyIterable()))
        .thenReturn(Flux.just(anime, anime.withName("")));

    webTestClient.post().uri("/animes/batch").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved))).exchange().expectStatus().isBadRequest()
        .expectBody().jsonPath("$.status").isEqualTo(400);
  }

  @Test
  @DisplayName("update save updated anime and returns empty Mono when successful")
  public void update_SaveUpdatedAnime_WhenSuccessful() {
    BDDMockito.when(animeRepositoryMock.save(anime)).thenReturn(Mono.empty());

    webTestClient.put().uri("/animes/{id}", 1).contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(anime)).exchange().expectStatus().isNoContent();
  }

  @Test
  @DisplayName("update returns Mono error when anime does exist")
  public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

    webTestClient.put().uri("/animes/{id}", 1).contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(anime)).exchange().expectStatus().isNotFound().expectBody().jsonPath("$.status")
        .isEqualTo(404).jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
  }

  @Test
  @DisplayName("delete removes the anime when successful")
  public void delete_RemovesAnime_WhenSuccessful() {
    webTestClient.delete().uri("/animes/{id}", 1).exchange().expectStatus().isNoContent();
  }

  @Test
  @DisplayName("delete returns Mono error when anime does not exist")
  public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

    webTestClient.delete().uri("/animes/{id}", 1).exchange().expectStatus().isNotFound().expectBody()
        .jsonPath("$.status").isEqualTo(404).jsonPath("$.developerMessage")
        .isEqualTo("A ResponseStatusException Happened");
  }
}
