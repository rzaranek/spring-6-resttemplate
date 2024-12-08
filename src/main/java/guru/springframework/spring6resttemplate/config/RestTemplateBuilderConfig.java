package guru.springframework.spring6resttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateBuilderConfig {

    // odwołanie się do konfiguracji środowiska (np. z pliku application.properties)
    @Value("${rest.template.rootUrl}")
    String rootUrl;

    // wykorzystanie klasy RestTemplateBuilderConfigurer pozwala wczytać domyślną konfigurację zmieniając tylko  wybrane
    @Bean
    RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {

        // sprawdzenie czy konfiguracja została ustawiona
        assert rootUrl != null;

        RestTemplateBuilder restTemplateBuilder = configurer.configure(new RestTemplateBuilder());
        DefaultUriBuilderFactory uriBuilderFactory =
                new DefaultUriBuilderFactory(rootUrl);

        return restTemplateBuilder.uriTemplateHandler(uriBuilderFactory);
    }
}
