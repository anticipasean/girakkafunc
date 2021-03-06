package cyclops.async.reactive.futurestream.react.lazy;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.async.reactive.futurestream.LazyReact;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;

public class FastFutureConversionTest {

    @Test
    public void conversion() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello");
        assertThat(new LazyReact().from(future)
                                  .peek(System.out::println)
                                  .then(action -> "result")
                                  .singleOrElse(null),
                   equalTo("result"));


    }

    @Test
    public void conversion2() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "hello2");
        assertThat(new LazyReact().from(future1,
                                        future2)
                                  .peek(System.out::println)
                                  .then(action -> "result")
                                  .toList()
                                  .size(),
                   equalTo(2));


    }
}
