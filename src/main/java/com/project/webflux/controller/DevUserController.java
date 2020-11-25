package com.project.webflux.controller;

import javax.validation.Valid;

import com.project.webflux.domain.DevUser;
import com.project.webflux.service.DevUserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class DevUserController {

  private final DevUserService devUserService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a user", tags = { "User" })
  public Mono<DevUser> save(@Valid @RequestBody DevUser devUser) {
    return devUserService.save(devUser);
  }

}
