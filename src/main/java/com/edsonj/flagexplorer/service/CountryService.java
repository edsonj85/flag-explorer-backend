package com.edsonj.flagexplorer.service;

import com.edsonj.flagexplorer.model.Country;
import com.edsonj.flagexplorer.model.CountryDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryService {
    Flux<Country> getAllCountries();
    Mono<CountryDetails> getCountryByName(String name);
}
