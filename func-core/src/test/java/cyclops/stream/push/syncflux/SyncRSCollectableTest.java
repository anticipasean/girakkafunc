package cyclops.stream.push.syncflux;

import cyclops.container.foldable.Foldable;
import cyclops.reactive.companion.Spouts;
import cyclops.stream.CollectableTest;
import reactor.core.publisher.Flux;

public class SyncRSCollectableTest extends CollectableTest {


    public <T> Foldable<T> of(T... values) {
        return Spouts.from(Flux.just(values));
    }

}
