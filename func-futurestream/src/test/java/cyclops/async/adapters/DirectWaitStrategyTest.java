package cyclops.async.adapters;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.async.strategy.wait.DirectWaitStrategy;
import cyclops.async.strategy.wait.WaitStrategy.Offerable;
import cyclops.async.strategy.wait.WaitStrategy.Takeable;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.agrona.concurrent.ManyToOneConcurrentArrayQueue;
import org.junit.Test;

public class DirectWaitStrategyTest {

    int called = 0;
    Takeable<String> takeable = () -> {
        called++;
        return null;
    };
    Offerable offerable = () -> {
        called++;
        return false;
    };


    @Test
    public void stream2() {
        Spliterator<String> split = Stream.of("hello",
                                              "world")
                                          .map(s -> "prefix-" + s)
                                          .spliterator();

        Stream<String> replayable1 = StreamSupport.stream(split,
                                                          false);
        Stream<String> replayable2 = StreamSupport.stream(split,
                                                          false);

        replayable1.forEach(System.out::println);
        replayable2.forEach(System.out::println);
    }

    @Test
    public void testTakeable() throws InterruptedException {
        called = 0;
        String result = new DirectWaitStrategy<String>().take(takeable);
        assertTrue(result == null);
        assertThat(called,
                   equalTo(1));
    }

    @Test
    public void testOfferable() throws InterruptedException {
        called = 0;
        boolean result = new DirectWaitStrategy<String>().offer(offerable);
        assertThat(result,
                   equalTo(false));
        assertThat(called,
                   equalTo(1));
    }

    @Test
    public void testwithQueue() {
        cyclops.async.adapters.Queue<String> q = new cyclops.async.adapters.Queue<>(new ManyToOneConcurrentArrayQueue<String>(100),
                                                                                    new DirectWaitStrategy<>(),
                                                                                    new DirectWaitStrategy<>());

        q.offer("hello");
        assertThat(q.get(),
                   equalTo("hello"));
    }

}
