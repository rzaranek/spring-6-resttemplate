package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

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

    @Test
    void createNewBeer() {

        BeerDTO newBeer = new BeerDTO();

        newBeer.setBeerName("My favorite newBeer");
        newBeer.setBeerStyle(BeerStyle.PILSNER);
        newBeer.setUpc("123456789");
        newBeer.setPrice(BigDecimal.valueOf(100));

        BeerDTO savedBeer = beerClient.createBeer(newBeer);

        assertNotNull(savedBeer);
    }

    @Test
    void updateBeer() {
        BeerDTO newBeer = new BeerDTO();
        newBeer.setBeerName("My favorite newBeer");
        newBeer.setBeerStyle(BeerStyle.PILSNER);
        newBeer.setUpc("123456789");
        newBeer.setPrice(BigDecimal.valueOf(100));
        BeerDTO savedBeer = beerClient.createBeer(newBeer);

        savedBeer.setBeerName("My super updatedBeer");
        BeerDTO updatedBeer = beerClient.updateBeer(savedBeer);

        assertNotNull(updatedBeer);
        assertEquals(savedBeer.getBeerName(), updatedBeer.getBeerName());
    }

    @Test
    void deleteBeer(){

        UUID id = beerClient.listBeers().getContent().getFirst().getId();

        beerClient.deleteBeer(id);

        assertThrows(HttpClientErrorException.class, () -> beerClient.getBeerById(id));
    }
}