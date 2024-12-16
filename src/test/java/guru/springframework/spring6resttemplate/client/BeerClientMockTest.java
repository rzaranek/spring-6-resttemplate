package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImp;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static guru.springframework.spring6resttemplate.client.BeerClientImpl.GET_BEER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BeerClientImpl.class)
public class BeerClientMockTest {

    static final String URL = "http://localhost:8080";

    @Autowired
    BeerClient beerClient;

    @Autowired
    MockRestServiceServer mockServer;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testListBeers() throws JsonProcessingException {

        String payload = objectMapper.writeValueAsString(getPage());

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(URL + GET_BEER_PATH))
                .andRespond(withSuccess(payload, APPLICATION_JSON));

        Page<BeerDTO> beerDTOS = beerClient.listBeers();

        assertThat(beerDTOS.getContent().size()).isGreaterThan(0);
    }

    BeerDTO getBeerDto(){
        BeerDTO beerDTO = new BeerDTO();
        beerDTO.setId(UUID.randomUUID());
        beerDTO.setPrice(new BigDecimal("10.99"));
        beerDTO.setBeerName("Mango Bobs");
        beerDTO.setBeerStyle(BeerStyle.IPA);
        beerDTO.setQuantityOnHand(500);
        beerDTO.setUpc("123245");
        return beerDTO;
    }

    BeerDTOPageImp getPage(){
        return new BeerDTOPageImp(Arrays.asList(getBeerDto()), 1, 25, 1);
    }
}
