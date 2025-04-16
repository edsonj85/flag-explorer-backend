package com.edsonj.flagexplorer.service;

import com.edsonj.flagexplorer.model.CountryApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CountryServiceImplTest {

    private WebClient.Builder mockBuilder;
    private WebClient mockWebClient;
    private WebClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;
    private WebClient.RequestHeadersSpec mockRequestHeadersSpec;
    private WebClient.ResponseSpec mockResponseSpec;

    private CountryService countryService;

    @BeforeEach
    public void setup() {
        mockBuilder = mock(WebClient.Builder.class);
        mockWebClient = mock(WebClient.class);
        mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockBuilder.baseUrl(any())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockWebClient);

        countryService = new CountryServiceImpl(mockBuilder);
    }

    @Test
    public void testGetAllCountries() {
        CountryApiResponse apiResponse = new CountryApiResponse();
        CountryApiResponse.Name name = new CountryApiResponse.Name();
        name.setCommon("Zimbabwe");
        apiResponse.setName(name);
        CountryApiResponse.Flags flags = new CountryApiResponse.Flags();
        flags.setPng("http://edsonj.com/flag.png");
        apiResponse.setFlags(flags);

        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri("/all")).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToFlux(CountryApiResponse.class)).thenReturn(Flux.just(apiResponse));

        StepVerifier.create(countryService.getAllCountries())
                .expectNextMatches(c -> c.getName().equals("Zimbabwe") && c.getFlag().equals("http://edsonj.com/flag.png"))
                .verifyComplete();
    }

    @Test
    public void testGetCountryDetails() {
        CountryApiResponse apiResponse = new CountryApiResponse();
        CountryApiResponse.Name name = new CountryApiResponse.Name();
        name.setCommon("Zimbabwe");
        apiResponse.setName(name);
        CountryApiResponse.Flags flags = new CountryApiResponse.Flags();
        flags.setPng("http://edsonj.com/flag.png");
        apiResponse.setFlags(flags);
        apiResponse.setCapital(Collections.singletonList("Harare"));
        apiResponse.setPopulation(1000000);

        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri("/name/{name}", "Zimbabwe")).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToFlux(CountryApiResponse.class)).thenReturn(Flux.just(apiResponse));

        StepVerifier.create(countryService.getCountryByName("Zimbabwe"))
                .expectNextMatches(d -> d.getName().equals("Zimbabwe") &&
                        d.getCapital().equals("Harare") &&
                        d.getPopulation() == 1000000 &&
                        d.getFlag().equals("http://edsonj.com/flag.png"))
                .verifyComplete();
    }
}