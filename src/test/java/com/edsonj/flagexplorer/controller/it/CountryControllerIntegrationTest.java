package com.edsonj.flagexplorer.controller.it;

import com.edsonj.flagexplorer.model.Country;
import com.edsonj.flagexplorer.model.CountryDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CountryControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGetAllCountries_success() {
        webTestClient.get()
                .uri("/countries")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Country.class)
                .value(countries -> {
                    assert !countries.isEmpty(); // Ensure list is not empty
                    assert countries.get(0).getName() != null;
                });
    }

    @Test
    public void testGetCountryByName_success() {
        webTestClient.get()
                .uri("/countries/Zimbabwe")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CountryDetails.class)
                .value(details -> {
                    assert details.getName().equalsIgnoreCase("Zimbabwe");
                    assert details.getCapital() != null;
                });
    }

    @Test
    public void testGetCountryByName_notFound() {
        webTestClient.get()
                .uri("/countries/MyName")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo("External API Error")
                .jsonPath("$.detail").value(detail -> detail.toString().contains("Not Found"));
    }
}
