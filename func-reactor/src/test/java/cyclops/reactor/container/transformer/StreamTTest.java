package cyclops.reactor.container.transformer;


import static cyclops.reactor.container.higherkinded.MonoAnyM.liftM;

import cyclops.monads.AnyM;
import cyclops.monads.AnyMs;
import cyclops.monads.Witness;
import cyclops.monads.Witness.list;
import cyclops.monads.Witness.optional;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.collection.container.mutable.ListX;
import cyclops.reactor.container.MonoT;
import cyclops.reactor.container.higherkinded.FluxAnyM;
import cyclops.reactor.stream.FluxReactiveSeq;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by johnmcclean on 09/06/2017.
 */
public class StreamTTest {

    @Test
    public void monoTypes() {
        ListX<Mono<Integer>> nested = ListX.of(Mono.just(10));
        MonoT<list, Integer> listOfMonos = liftM(AnyM.fromList(nested));
        MonoT<list, Integer> doubled = listOfMonos.map(i -> i * 2);
    }

    @Test
    public void types() {
        ListX<Flux<Integer>> nested = ListX.of(Flux.just(10));
        StreamT<list, Integer> listOfFluxs = FluxAnyM.liftM(AnyM.fromList(nested));
        StreamT<list, Integer> doubled = listOfFluxs.map(i -> i * 2);

    }

    @Test
    public void types2() {
        ReactiveSeq<Integer> reactive = FluxReactiveSeq.just(1,
                                                             2,
                                                             3);
        StreamT<optional, Integer> transformer = AnyMs.liftM(reactive,
                                                             optional.INSTANCE);
    }

    @Test
    public void extractMono() {
        MonoT<list, Integer> trans = liftM(AnyM.fromList(ListX.of(Mono.just(1))));

        AnyM<list, Mono<Integer>> anyM = trans.unwrap();
        System.out.println(anyM);
    }

    @Test
    public void extract() {
        StreamT<list, Integer> trans = AnyMs.liftM(FluxReactiveSeq.just(1,
                                                                        2,
                                                                        3),
                                                   list.INSTANCE);

        AnyM<list, Flux<Integer>> anyM = trans.unwrapTo(FluxAnyM::fromStream);
        System.out.println(anyM);
    }

    @Test
    public void moreExtract() {
        StreamT<list, Integer> trans = AnyMs.liftM(FluxReactiveSeq.just(1,
                                                                        2,
                                                                        3),
                                                   list.INSTANCE);

        ListX<Flux<Integer>> listObs = Witness.list(trans.unwrapTo(FluxAnyM::fromStream));
        System.out.println(listObs);
    }
}
