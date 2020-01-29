package com.essec.microservices;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class HystrixFallbackController {
    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("system busy,please try later");
    }
}