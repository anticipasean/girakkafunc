package cyclops.reactive.collection.container;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import cyclops.container.immutable.tuple.Tuple;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.collection.container.mutable.ListX;
import cyclops.reactive.companion.Spouts;
import cyclops.reactive.subscriber.AsyncSubscriber;
import java.util.concurrent.ForkJoinPool;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Created by johnmcclean on 20/01/2017.
 */
public class ZipAsyncTest {

    protected <U> ReactiveSeq<U> flux(U... array) {
        return Spouts.from(Flux.just(array)
                               .subscribeOn(Schedulers.fromExecutor(ForkJoinPool.commonPool())));

    }

    @Test
    public void asyncZipSimple() {
        /**
         nextAsync().printOut();
         Spouts.of(1,2,3,4,5)
         .zipWithStream(nextAsync()).printOut();
         Spouts.of(1,2,3,4,5)
         .zipWithStream(Spouts.of(1,2)).printOut();
         **/
        /**
         Spouts.of(1,2,3,4,5)
         .zipWithStream(nextAsync()).forEach(System.out::println,
         System.err::println);
         **/

        /**
         Spouts.of(1,2,3,4,5)
         .zipWithStream(nextAsync())
         .listX()
         .printOut();
         **/

        ListX<Tuple2<Integer, Integer>> list = Spouts.of(1,
                                                         2,
                                                         3,
                                                         4,
                                                         5)
                                                     .peek(System.out::println)
                                                     .zipWithStream(nextAsync())
                                                     .to(ReactiveConvertableSequence::converter)
                                                     .listX();

        System.out.println("List creation is non-blocking");

        list.printOut();
        System.out.println("Print out the list asynchronously");


    }

    @Test
    public void asyncZip() {
        System.out.println(Thread.currentThread()
                                 .getId());
        Spouts.of(1,
                  2,
                  3,
                  4,
                  5)
              .zipWithStream(nextAsync())
              .grouped(2)
              .flatMap(i -> i.stream())
              .to(ReactiveConvertableSequence::converter)
              .listX()
              .materialize()
              .printOut();

        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5)
                         .zipWithStream(nextAsync())
                         .grouped(2)
                         .flatMap(i -> i.stream())
                         .to(ReactiveConvertableSequence::converter)
                         .listX(),
                   equalTo(ListX.of(Tuple.tuple(1,
                                                1),
                                    Tuple.tuple(2,
                                                2))));

    }

    private ReactiveSeq<Integer> nextAsync() {
        AsyncSubscriber<Integer> sub = Spouts.asyncSubscriber();
        new Thread(() -> {

            sub.awaitInitialization();
            try {
                //not a reactive-stream so we don't know with certainty when demand signalled
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sub.onNext(1);
            sub.onNext(2);
            sub.onComplete();
        }).start();
        return sub.stream();
    }
}
