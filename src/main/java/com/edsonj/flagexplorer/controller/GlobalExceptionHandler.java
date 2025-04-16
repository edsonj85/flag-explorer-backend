package com.edsonj.flagexplorer.controller;

import com.edsonj.flagexplorer.exceptions.CountryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CountryNotFoundException.class)
    public Mono<ProblemDetail> handleCountryNotFound(CountryNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Country Not Found");
        problem.setDetail(ex.getMessage());
        problem.setProperty("timestamp", System.currentTimeMillis());
        return Mono.just(problem);
    }

    @ExceptionHandler(WebClientRequestException.class)
    public Mono<ProblemDetail> handleWebClientRequestException(WebClientRequestException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("External API Unreachable");
        problem.setDetail("Unable to connect to the country API. Please try again later.");
        problem.setProperty("timestamp", System.currentTimeMillis());
        return Mono.just(problem);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ProblemDetail> handleWebClientResponseException(WebClientResponseException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());
        problem.setTitle("External API Error");
        problem.setDetail("Error from external API: " + ex.getStatusText());
        problem.setProperty("timestamp", System.currentTimeMillis());
        return Mono.just(problem);
    }
}
