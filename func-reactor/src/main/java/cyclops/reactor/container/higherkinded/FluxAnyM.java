package cyclops.reactor.container.higherkinded;

import com.oath.cyclops.anym.AnyMSeq;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.monads.XorM;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactor.companion.Fluxs;
import cyclops.reactor.container.higherkinded.ReactorWitness.flux;
import cyclops.reactor.container.transformer.StreamT;
import cyclops.reactor.stream.FluxReactiveSeq;
import cyclops.reactor.stream.impl.FluxReactiveSeqImpl;
import java.util.function.Function;
import java.util.stream.Stream;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public interface FluxAnyM {

    static <W1 extends WitnessType<W1>, T> XorM<W1, flux, T> xorM(Flux<T> type) {
        return XorM.right(anyM(type));
    }

    static <T> Flux<T> raw(AnyM<flux, T> anyM) {
        return ReactorWitness.flux(anyM);
    }

    static <T, W extends WitnessType<W>> AnyM<W, Flux<T>> fromStream(AnyM<W, Stream<T>> anyM) {
        return anyM.map(s -> Fluxs.fluxFrom(ReactiveSeq.fromStream(s)));
    }

    static <W extends WitnessType<W>, T> StreamT<W, T> fluxify(StreamT<W, T> nested) {
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        AnyM<W, ReactiveSeq<T>> flowableM = anyM.map(s -> {
            if (s instanceof FluxReactiveSeqImpl) {
                return (FluxReactiveSeqImpl) s;
            }
            if (s instanceof ReactiveSeq) {
                return ((ReactiveSeq<T>) s).fold(sync -> new FluxReactiveSeqImpl<T>(Flux.fromStream(sync)),
                                                 rs -> new FluxReactiveSeqImpl<T>(Flux.from(rs)),
                                                 async -> new FluxReactiveSeqImpl<T>(Flux.from(async)));
            }
            return new FluxReactiveSeqImpl<T>(Flux.fromStream(s));
        });
        StreamT<W, T> res = StreamT.of(flowableM);
        return res;
    }


    static <W extends WitnessType<W>, T, R> R nestedFlux(StreamT<W, T> nested,
                                                         Function<? super AnyM<W, Flux<T>>, ? extends R> mapper) {
        return mapper.apply(nestedFlux(nested));
    }

    static <W extends WitnessType<W>, T> AnyM<W, Flux<T>> nestedFlux(StreamT<W, T> nested) {
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        return anyM.map(s -> {
            if (s instanceof FluxReactiveSeqImpl) {
                return ((FluxReactiveSeqImpl) s).getFlux();
            }
            if (s instanceof ReactiveSeq) {
                ReactiveSeq<T> r = (ReactiveSeq<T>) s;
                return r.fold(sync -> Flux.fromStream(sync),
                              rs -> Flux.from((Publisher) s),
                              async -> Flux.from(async));
            }
            if (s instanceof Publisher) {
                return Flux.from((Publisher) s);
            }
            return Flux.fromStream(s);
        });
    }

    static <T, W extends WitnessType<W>> StreamT<W, T> liftM(Flux<T> opt,
                                                             W witness) {
        return StreamT.of(witness.adapter()
                                 .unit(FluxReactiveSeq.reactiveSeq(opt)));
    }

    static <W extends WitnessType<W>, T> StreamT<W, T> liftM(AnyM<W, Flux<T>> nested) {
        AnyM<W, ReactiveSeq<T>> monad = nested.map(s -> new FluxReactiveSeqImpl<T>(s));
        return StreamT.of(monad);
    }


    /**
     * Construct an AnyM type from a Flux. This allows the Flux to be manipulated according to a standard interface along with a
     * vast array of other Java Monad implementations
     *
     * <pre>
     * {@code
     *
     *    AnyMSeq<Integer> flux = Fluxs.anyM(Flux.just(1,2,3));
     *    AnyMSeq<Integer> transformedFlux = myGenericOperation(flux);
     *
     *    public AnyMSeq<Integer> myGenericOperation(AnyMSeq<Integer> monad);
     * }
     * </pre>
     *
     * @param flux To wrap inside an AnyM
     * @return AnyMSeq wrapping a flux
     */
    static <T> AnyMSeq<flux, T> anyM(Flux<T> flux) {
        return AnyM.ofSeq(FluxReactiveSeq.reactiveSeq(flux),
                          ReactorWitness.flux.INSTANCE);
    }

    static <T> Flux<T> flux(AnyM<flux, T> flux) {

        FluxReactiveSeqImpl<T> fluxSeq = flux.unwrap();
        return fluxSeq.getFlux();
    }

}
