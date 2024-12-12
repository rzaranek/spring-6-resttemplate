package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImp;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    @Value("${rest.template.get-beer-path}")
    String getBeerPath;

    private final RestTemplateBuilder restTemplateBuilder;

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(getBeerPath);

        if (beerName != null && !beerName.isEmpty())
            uriComponentsBuilder.queryParam("beerName", beerName);

        if (beerStyle != null)
            uriComponentsBuilder.queryParam("beerStyle", beerStyle.toString());

        if (showInventory != null)
            uriComponentsBuilder.queryParam("showInventory", showInventory.toString());

        if (pageNumber != null && pageSize != null) {
            uriComponentsBuilder.queryParam("pageNumber", pageNumber.toString());
            uriComponentsBuilder.queryParam("pageSize", pageSize.toString());
        }

        ResponseEntity<BeerDTOPageImp> response =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImp.class);

        return response.getBody();
    }

    @Override
    public Page<BeerDTO> listBeers() {
        return listBeers(null, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName) {
        return listBeers(beerName, null, null, null, null);
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

    public static String GET_BEER_BY_ID_PATH = "/api/v1/beer/{id}";

    @Override
    public BeerDTO getBeerById(UUID beerId) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO newBeer) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        URI uri = restTemplate.postForLocation(GET_BEER_PATH, newBeer);
        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO updatedBeer) {

        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.put(GET_BEER_BY_ID_PATH, updatedBeer, updatedBeer.getId());

        return getBeerById(updatedBeer.getId());
    }

}
