package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    @Value("${rest.template.get-beer-path}")
    String getBeerPath;

    private final RestTemplateBuilder restTemplateBuilder;

    @Override
    public Page<BeerDTO> listBeers() {

        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<BeerDTOPageImp> responseEntity =
                restTemplate.getForEntity(getBeerPath, BeerDTOPageImp.class);

        System.out.println(
                responseEntity.getBody().getNumberOfElements());

        return null;
    }

    public static final String GET_BEER_PATH = "/api/v1/beer";

    @Override
    public Page<BeerDTO> listBeersDemo() {

        RestTemplate restTemplate = restTemplateBuilder.build();

        // get list as a String
        ResponseEntity<String> stringResponseEntity =
                restTemplate.getForEntity(GET_BEER_PATH, String.class);

        // get list as a Map
        ResponseEntity<Map> mapResponseEntity =
                restTemplate.getForEntity(GET_BEER_PATH, Map.class);

        // get list as a Json
        ResponseEntity<JsonNode> jsonNodeResponseEntity =
                restTemplate.getForEntity(GET_BEER_PATH, JsonNode.class);
        jsonNodeResponseEntity.getBody().findPath("content")
                .elements().forEachRemaining(jsonNode ->
                        System.out.println(jsonNode.get("beerName").asText()));

        System.out.println(stringResponseEntity.getBody());

        return null;
    }
}
