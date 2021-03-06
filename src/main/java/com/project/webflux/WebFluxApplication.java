package com.project.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.blockhound.BlockHound;

@SpringBootApplication
public class WebFluxApplication {

  static {
    BlockHound.install();
  }

  public static void main(String[] args) {
    SpringApplication.run(WebFluxApplication.class, args);
  }

}
