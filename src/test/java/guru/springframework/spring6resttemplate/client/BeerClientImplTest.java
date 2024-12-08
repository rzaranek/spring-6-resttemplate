package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void listBeers() {

        Page<BeerDTO> beers =
                beerClient.listBeers();

        System.out.println(beers.stream().count());

        beers.forEach(beerDTO -> {
            System.out.println(beerDTO.getBeerName());
        });

    }

    @Test
    void listBeersBeerName() {

        Page<BeerDTO> beers =
                beerClient.listBeers("PORTER");

        System.out.println(beers.stream().count());

        beers.forEach(beerDTO -> {
            System.out.println(beerDTO.getBeerName());
        });

    }

    @Test
    void getBeerById() {

        UUID id = beerClient.listBeers().getContent().getFirst().getId();

        BeerDTO beer = beerClient.getBeerById(id);

        System.out.println(beer.getBeerName());

        assertEquals(id, beer.getId());
        assertNotNull(beer.getId());

    }
}