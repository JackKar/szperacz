package pl.ec.kafka.szperacz.search.preprocessing.search;

import java.util.List;
import lombok.Data;

@Data
public class SearchPreprocessingResponse {

    private final List<BufferedEvent> events;
}
