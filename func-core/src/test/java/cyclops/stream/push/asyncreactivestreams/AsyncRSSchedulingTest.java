package cyclops.stream.push.asyncreactivestreams;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.companion.Spouts;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class AsyncRSSchedulingTest {

    ScheduledExecutorService ex = Executors.newScheduledThreadPool(1);
    AtomicInteger count = new AtomicInteger(0);

    protected <U> ReactiveSeq<U> of(U... array) {
        return Spouts.from(Flux.just(array)
                               .subscribeOn(Schedulers.fromExecutor(ForkJoinPool.commonPool())));

    }

    @Test
    public void cronTest() throws InterruptedException {
        of(1,
           2,
           3,
           4).peek(i -> count.incrementAndGet())
             .peek(System.out::println)
             .schedule("* * * * * ?",
                       ex);

        Thread.sleep(5000);

    }

    @Test
    public void cronDebounceTest() throws InterruptedException {
        assertThat(of(1,
                      2,
                      3,
                      4).peek(i -> count.incrementAndGet())
                        .peek(System.out::println)
                        .schedule("* * * * * ?",
                                  ex)
                        .connect()
                        .debounce(1,
                                  TimeUnit.DAYS)
                        .peek(System.out::println)
                        .toList(),
                   equalTo(Arrays.asList(1)));


    }

    @Test
    public void fixedRateTest() throws InterruptedException {
        assertThat(of(1,
                      2,
                      3,
                      4).peek(i -> count.incrementAndGet())
                        .peek(System.out::println)
                        .scheduleFixedRate(1000,
                                           ex)
                        .connect()
                        .debounce(1,
                                  TimeUnit.DAYS)
                        .peek(System.out::println)
                        .toList(),
                   equalTo(Arrays.asList(1)));


    }

    @Test
    public void fixedRateDelay() throws InterruptedException {
        assertThat(of(1,
                      2,
                      3,
                      4).peek(i -> count.incrementAndGet())
                        .peek(System.out::println)
                        .scheduleFixedDelay(1000,
                                            ex)
                        .connect()
                        .debounce(1,
                                  TimeUnit.DAYS)
                        .peek(System.out::println)
                        .toList(),
                   equalTo(Arrays.asList(1)));


    }
}
