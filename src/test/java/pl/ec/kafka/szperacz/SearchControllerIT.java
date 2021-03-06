package pl.ec.kafka.szperacz;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import java.time.LocalDateTime;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import pl.ec.kafka.szperacz.search.kafka.Events;
import pl.ec.kafka.szperacz.search.kafka.SearchRequest;
import pl.ec.kafka.szperacz.search.kafka.SearchResponse;
import pl.ec.kafka.szperacz.search.preprocessing.model.MapCluster;
import pl.ec.kafka.szperacz.search.preprocessing.search.SearchPreprocessingRequest;
import pl.ec.kafka.szperacz.search.preprocessing.search.SearchPreprocessingResponse;

@Disabled
@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(environments = "integration")
class SearchControllerIT {

    @Inject
    @Client("/api/szperacz")
    private RxHttpClient client;

    @Test
    void shouldSearchForEvents() {
        // given
        String topic = "sorted_out";
        String from = "04-02-2021T12:50:00";
        String to = "04-02-2021T13:50:00";
        String deviceId = "4935";

        // when
        var actual = client.toBlocking().retrieve(
            HttpRequest.GET("/topic=" + topic + "&from=" + from + "&to=" + to + "&deviceId=" + deviceId),
            Argument.listOf(Events.class));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldSearchPost() {
        // given
        var request = new SearchRequest(
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(1),
            List.of("sorted_out", "processed_out"),
            "4935");

        // when
        var actual = client.toBlocking().retrieve(
            HttpRequest.POST("/search", request),
            Argument.of(SearchResponse.class));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldDoPreprocessingSearch() {
        // given
        var request = new SearchPreprocessingRequest(
            LocalDateTime.of(2021, 3, 17, 7, 50, 0),
            LocalDateTime.of(2021, 3, 17, 9, 50, 0),
            "sorted_out", "buffered_out", "preprocessed_out",
            "5554");

        // when
        var actual = client.toBlocking().retrieve(
            HttpRequest.POST("/searchPreprocessing", request),
            Argument.of(SearchPreprocessingResponse.class));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldSearchForGantry() {
        // given
        String gantry = "A01_0006,4_TF_0";

        // when
        var actual = client.toBlocking().retrieve(
            HttpRequest.GET("/maps/gantry=" + gantry),
            Argument.of(MapCluster.class));

        // then
        assertNull(actual);
    }
}
