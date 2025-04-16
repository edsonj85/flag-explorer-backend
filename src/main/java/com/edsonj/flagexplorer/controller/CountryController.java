package com.edsonj.flagexplorer.controller;

import com.edsonj.flagexplorer.exceptions.CountryNotFoundException;
import com.edsonj.flagexplorer.model.Country;
import com.edsonj.flagexplorer.model.CountryDetails;
import com.edsonj.flagexplorer.service.CountryService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/countries")
public class CountryController {

    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Country>>> getAllCountries() {
        return Mono.just(ResponseEntity.ok(service.getAllCountries()));
    }

    @GetMapping("/{name}")
    public Mono<ResponseEntity<CountryDetails>> getCountryByName(@NotNull  @PathVariable String name) {
        return service.getCountryByName(name)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new CountryNotFoundException(name)));
    }
}
