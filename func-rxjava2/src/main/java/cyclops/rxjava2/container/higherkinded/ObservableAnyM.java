package cyclops.rxjava2.container.higherkinded;

import static cyclops.rxjava2.companion.Observables.anyM;

import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.monads.XorM;
import cyclops.rxjava2.adapter.ObservableReactiveSeqImpl;
import cyclops.rxjava2.companion.Observables;
import cyclops.rxjava2.container.higherkinded.Rx2Witness.observable;
import cyclops.reactor.container.transformer.StreamT;
import cyclops.reactive.ReactiveSeq;
import io.reactivex.Observable;
import java.util.function.Function;
import java.util.stream.Stream;
import org.reactivestreams.Publisher;

@Deprecated
public interface ObservableAnyM {

    public static <W1 extends WitnessType<W1>, T> XorM<W1, observable, T> xorM(Observable<T> type) {
        return XorM.right(anyM(type));
    }

    public static <T, W extends WitnessType<W>> AnyM<W, Observable<T>> fromStream(AnyM<W, Stream<T>> anyM) {
        return anyM.map(s -> Observables.fromStream(s));
    }

    public static <T> Observable<T> raw(AnyM<observable, T> anyM) {
        return Rx2Witness.observable(anyM);
    }

    public static <W extends WitnessType<W>, T> StreamT<W, T> observablify(StreamT<W, T> nested) {
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        AnyM<W, ReactiveSeq<T>> fluxM = anyM.map(s -> {
            if (s instanceof ObservableReactiveSeqImpl) {
                return (ObservableReactiveSeqImpl) s;
            }
            if (s instanceof ReactiveSeq) {
                return new ObservableReactiveSeqImpl<T>(Observables.observableFrom((ReactiveSeq<T>) s));
            }
            if (s instanceof Publisher) {
                return new ObservableReactiveSeqImpl<T>(Observables.observable((Publisher) s));
            }
            return new ObservableReactiveSeqImpl<T>(Observables.fromStream(s));
        });
        StreamT<W, T> res = StreamT.of(fluxM);
        return res;
    }

    public static <W extends WitnessType<W>, T, R> R nestedObservable(StreamT<W, T> nested,
                                                                      Function<? super AnyM<W, Observable<T>>, ? extends R> mapper) {
        return mapper.apply(nestedObservable(nested));
    }

    public static <W extends WitnessType<W>, T> AnyM<W, Observable<T>> nestedObservable(StreamT<W, T> nested) {
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        return anyM.map((Stream<T> s) -> {
            if (s instanceof ObservableReactiveSeqImpl) {
                return ((ObservableReactiveSeqImpl) s).getObservable();
            }
            if (s instanceof ReactiveSeq) {
                return Observables.observableFrom((ReactiveSeq<T>) s);
            }
            if (s instanceof Publisher) {
                return Observables.observable((Publisher) s);
            }
            return Observables.fromStream(s);
        });
    }

    public static <W extends WitnessType<W>, T> StreamT<W, T> liftM(AnyM<W, Observable<T>> nested) {

        AnyM<W, ReactiveSeq<T>> monad = nested.map(s -> new ObservableReactiveSeqImpl<T>(s));
        return StreamT.of(monad);
    }
}
