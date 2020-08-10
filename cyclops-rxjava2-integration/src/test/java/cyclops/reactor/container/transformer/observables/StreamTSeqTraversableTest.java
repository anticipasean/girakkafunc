package cyclops.reactor.container.transformer.observables;

import cyclops.reactor.container.transformer.StreamT;
import cyclops.types.AbstractTraversableTest;
import cyclops.container.traversable.Traversable;
import cyclops.monads.AnyMs;
import cyclops.monads.ObservableAnyM;
import cyclops.monads.Witness;
import cyclops.monads.Witness.list;
import cyclops.pure.reactive.ObservableReactiveSeq;
import cyclops.pure.reactive.collections.mutable.ListX;
import io.reactivex.Observable;
import org.junit.Test;


public class StreamTSeqTraversableTest extends AbstractTraversableTest {

    @Override
    public <T> Traversable<T> of(T... elements) {
        return AnyMs.liftM(ObservableReactiveSeq.of(elements),
                           Witness.reactiveSeq.ITERATIVE);
    }

    @Override
    public <T> Traversable<T> empty() {

        return AnyMs.liftM(ObservableReactiveSeq.<T>empty(),
                           Witness.reactiveSeq.ITERATIVE);
    }

    @Test
    public void conversion() {
        StreamT<list, Integer> trans = AnyMs.liftM(ObservableReactiveSeq.just(1,
                                                                              2,
                                                                              3),
                                                   list.INSTANCE);

        ListX<Observable<Integer>> listObs = Witness.list(trans.unwrapTo(ObservableAnyM::fromStream));

    }

}