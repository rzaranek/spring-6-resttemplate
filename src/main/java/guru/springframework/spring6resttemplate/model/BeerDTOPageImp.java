package guru.springframework.spring6resttemplate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true, value = "pageable")
public class BeerDTOPageImp extends PageImpl<BeerDTO> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BeerDTOPageImp(@JsonProperty("content") List<BeerDTO> content,
                          @JsonProperty("number") int page,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") long total) {
        super(content, PageRequest.of(page, size), total);
    }

    public BeerDTOPageImp(List<BeerDTO> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public BeerDTOPageImp(List<BeerDTO> content) {
        super(content);
    }
}
