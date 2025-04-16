package com.edsonj.flagexplorer.controller;

import com.edsonj.flagexplorer.model.Country;
import com.edsonj.flagexplorer.model.CountryDetails;
import com.edsonj.flagexplorer.service.CountryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CountryController.class)
public class CountryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CountryService service;

    @Test
    public void testGetAllCountries() {
        Country country = new Country();
        country.setName("Zimbabwe");
        country.setFlag("http://edsonj.com/south_africa.png");

        when(service.getAllCountries()).thenReturn(Flux.just(country));

        webTestClient.get()
                .uri("/countries")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Country.class)
                .hasSize(1)
                .consumeWith(resp -> {
                    var c = Objects.requireNonNull(resp.getResponseBody()).get(0);
                    assertNotNull(c);
                    assertEquals(country.getName(), c.getName());
                    assertEquals(country.getFlag(), c.getFlag());
                });
    }

    @Test
    public void testGetCountryByName_found() {
        CountryDetails details = new CountryDetails();
        details.setName("Zimbabwe");
        details.setCapital("Harare");
        details.setPopulation(1000000);
        details.setFlag("http://edsonj.com/zimbabwe.png");

        when(service.getCountryByName("Zimbabwe")).thenReturn(Mono.just(details));

        webTestClient.get()
                .uri("/countries/{name}", "Zimbabwe")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CountryDetails.class)
                .consumeWith(response -> {
                    CountryDetails body = response.getResponseBody();
                    assert body != null;
                    assert body.getName().equals("Zimbabwe");
                    assert body.getCapital().equals("Harare");
                });
    }

    @Test
    public void testGetCountryByName_problemDetailWhenNotFound() {
        when(service.getCountryByName("Atlantis")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/countries/Atlantis")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Country Not Found")
                .jsonPath("$.detail").isEqualTo("Country not found: Atlantis");
    }

    @Test
    public void testGetAllCountries_whenExternalApiFails() {
        when(service.getAllCountries()).thenThrow(new WebClientRequestException(
                new RuntimeException("Timeout"), HttpMethod.GET, URI.create("https://restcountries.com/v3.1/all") ,
                HttpHeaders.EMPTY));

        webTestClient.get()
                .uri("/countries")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.title").isEqualTo("External API Unreachable");
    }
}