package cyclops.reactor.companion;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.async.Future;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class MonosTest {

    Mono<Integer> just;
    Mono<Integer> none;
    Mono<Integer> active;
    Mono<Integer> just2;

    @Before
    public void setup() {
        just = Mono.just(10);
        none = Mono.empty();
        none.toFuture()
            .completeExceptionally(new Exception("boo"));
        active = Mono.fromFuture(CompletableFuture::new);
        just2 = Mono.just(20);
    }

    @Test
    public void testSequenceError() throws InterruptedException {
        Mono<Flux<Integer>> maybes = Monos.sequence(Flux.just(just,
                                                              none));

        assertThat(Future.fromPublisher(maybes)
                         .isFailed(),
                   equalTo(true));
    }

    @Test
    public void testSequenceErrorAsync() {
        Mono<Flux<Integer>> maybes = Monos.sequence(Flux.just(just,
                                                              active));
        assertThat(Future.fromPublisher(maybes)
                         .isDone(),
                   equalTo(false));
    }

    @Test
    public void testSequenceTwo() {
        Mono<Flux<Integer>> maybes = Monos.sequence(Flux.just(just,
                                                              just2));
        assertThat(maybes.toFuture()
                         .join()
                         .toStream()
                         .collect(Collectors.toList()),
                   equalTo(Arrays.asList(10,
                                         20)));
    }

}
