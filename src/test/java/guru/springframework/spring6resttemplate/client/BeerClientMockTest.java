package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImp;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static guru.springframework.spring6resttemplate.client.BeerClientImpl.GET_BEER_BY_ID_PATH;
import static guru.springframework.spring6resttemplate.client.BeerClientImpl.GET_BEER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
public class BeerClientMockTest {

    static final String URL = "http://localhost:8080";

    BeerClient beerClient;

    MockRestServiceServer server;

    @Autowired
    RestTemplateBuilder restTemplateBuilderConfigured;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    RestTemplateBuilder mockRestTemplateBuilder =
            new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    BeerDTO beerDTO;
    String beerJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = restTemplateBuilderConfigured.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();

        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);

        beerClient = new BeerClientImpl(mockRestTemplateBuilder);

        beerDTO = getBeerDto();
        beerJson = objectMapper.writeValueAsString(beerDTO);
    }

    private void mockGetOperation() {
        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withSuccess(beerJson, APPLICATION_JSON));
    }

    @Test
    public void testGetBeerById() {

        mockGetOperation();

        BeerDTO responseDto = beerClient.getBeerById(beerDTO.getId());

        assertThat(responseDto.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void testCreateBeer() {

        URI beerUri = UriComponentsBuilder.fromPath(GET_BEER_BY_ID_PATH)
                .build(beerDTO.getId());

        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(URL + GET_BEER_PATH))
                .andRespond(withAccepted().location(beerUri));

        mockGetOperation();

        BeerDTO responseDto = beerClient.createBeer(beerDTO);
        assertThat(responseDto.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void testUpdateBeer() {

        server.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withNoContent());

        mockGetOperation();

        BeerDTO responseDto = beerClient.updateBeer(beerDTO);

        assertThat(responseDto.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void testDeleteBeer() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withNoContent());

        beerClient.deleteBeer(beerDTO.getId());

        server.verify();
    }

    @Test
    void testDeleteBeerNotFound() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class,
                () -> beerClient.deleteBeer(beerDTO.getId()));

        server.verify();
    }

    @Test
    public void testListBeers() throws JsonProcessingException {

        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(URL + GET_BEER_PATH))
                .andRespond(withSuccess(payload, APPLICATION_JSON));

        Page<BeerDTO> beerDTOS = beerClient.listBeers();

        assertThat(beerDTOS.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testListBeersWithParam() throws JsonProcessingException {

        String payload = objectMapper.writeValueAsString(getPage());

        URI uri = UriComponentsBuilder.fromUriString(URL + GET_BEER_PATH)
                .queryParam("beerName", "ALE")
                .build().toUri();

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                .andExpect(queryParam("beerName", "ALE"))
                .andRespond(withSuccess(payload, APPLICATION_JSON));

        Page<BeerDTO> beerDTOPage = beerClient.listBeers("ALE");

        assertThat(beerDTOPage.getContent().size()).isGreaterThan(0);
    }

    BeerDTO getBeerDto() {
        BeerDTO beerDTO = new BeerDTO();
        beerDTO.setId(UUID.randomUUID());
        beerDTO.setPrice(new BigDecimal("10.99"));
        beerDTO.setBeerName("Mango Bobs");
        beerDTO.setBeerStyle(BeerStyle.IPA);
        beerDTO.setQuantityOnHand(500);
        beerDTO.setUpc("123245");
        return beerDTO;
    }

    BeerDTOPageImp getPage() {
        return new BeerDTOPageImp(Arrays.asList(getBeerDto()), 1, 25, 1);
    }

    @Value("${rest.template.get-beer-path}")
    private String beerPath1;

    @Test
    void testValueFromProperties() {
        System.out.println(beerPath1);
    }
}
