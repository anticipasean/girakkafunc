package cyclops.streams.push.asyncreactivestreams;


import cyclops.container.foldable.Folds;
import cyclops.reactive.companion.Spouts;
import cyclops.streams.CollectableTest;
import java.util.concurrent.ForkJoinPool;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class AsyncRSCollectableTest extends CollectableTest {


    public <T> Folds<T> of(T... values) {

        return Spouts.from(Flux.just(values)
                               .subscribeOn(Schedulers.fromExecutor(ForkJoinPool.commonPool())));
    }

}
