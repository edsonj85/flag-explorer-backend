package com.edsonj.flagexplorer.service;

import com.edsonj.flagexplorer.model.Country;
import com.edsonj.flagexplorer.model.CountryApiResponse;
import com.edsonj.flagexplorer.model.CountryDetails;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Slf4j
@Service
public class CountryServiceImpl implements CountryService {

    private final WebClient webClient;

    private final String BASE_URL = "https://restcountries.com/v3.1";

    public CountryServiceImpl(WebClient.Builder webClientBuilder) {
        HttpClient httpClient = HttpClient.create()
                .secure(spec -> {
                    try {
                        spec.sslContext(SslContextBuilder.forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE).build());
                    } catch (SSLException e) {
                        throw new RuntimeException(e);
                    }
                });
        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(BASE_URL).build();
    }

    @Override
    public Flux<Country> getAllCountries() {
        log.info("Called for all countries");
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToFlux(CountryApiResponse.class)
                .map(api -> {
                    Country c = new Country();
                    c.setName(api.getName().getCommon());
                    c.setFlag(api.getFlags().getPng());
                    return c;
                });
    }

    @Override
    public Mono<CountryDetails> getCountryByName(String name) {
        log.info("Called for all country {}", name);
        return webClient.get()
                .uri("/name/{name}", name)
                .retrieve()
                .bodyToFlux(CountryApiResponse.class)
                .next()
                .map(api -> {
                    CountryDetails details = new CountryDetails();
                    details.setName(api.getName().getCommon());
                    details.setCapital(api.getCapital() != null && !api.getCapital().isEmpty() ? api.getCapital().get(0) : "N/A");
                    details.setPopulation(api.getPopulation());
                    details.setFlag(api.getFlags().getPng());
                    return details;
                });
    }
}


