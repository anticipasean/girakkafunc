package cyclops.rxjava2.adapter;


import com.oath.cyclops.anym.extensability.AbstractMonadAdapter;
import cyclops.rxjava2.companion.Functions;
import cyclops.rxjava2.companion.Maybes;
import cyclops.async.Future;
import cyclops.monads.AnyM;
import cyclops.rxjava2.container.higherkinded.MaybeAnyM;
import cyclops.rxjava2.container.higherkinded.Rx2Witness.maybe;
import cyclops.reactive.ReactiveSeq;
import io.reactivex.Maybe;
import java.util.function.Function;
import java.util.function.Predicate;

public class MaybeAdapter extends AbstractMonadAdapter<maybe> {

    public MaybeAdapter() {
    }

    @Override
    public <T> Iterable<T> toIterable(AnyM<maybe, T> t) {
        return Future.fromPublisher(future(t).toFlowable());
    }

    @Override
    public <T, R> AnyM<maybe, R> ap(AnyM<maybe, ? extends Function<? super T, ? extends R>> fn,
                                    AnyM<maybe, T> apply) {
        Maybe<T> f = future(apply);

        Maybe<? extends Function<? super T, ? extends R>> fnF = future(fn);

        Future<T> crF1 = Future.fromPublisher(f.toFlowable());
        Future<? extends Function<? super T, ? extends R>> crFnF = Future.fromPublisher(fnF.toFlowable());

        Maybe<R> res = Maybes.fromPublisher(crF1.zip(crFnF,
                                                     (a, b) -> b.apply(a)));
        return MaybeAnyM.anyM(res);

    }

    @Override
    public <T> AnyM<maybe, T> filter(AnyM<maybe, T> t,
                                     Predicate<? super T> fn) {

        return MaybeAnyM.anyM(future(t).filter(Functions.rxPredicate(fn)));
    }

    <T> Maybe<T> future(AnyM<maybe, T> anyM) {
        return anyM.unwrap();
    }

    <T> Future<T> futureW(AnyM<maybe, T> anyM) {
        return Future.fromPublisher(anyM.unwrap());
    }

    @Override
    public <T> AnyM<maybe, T> empty() {
        return MaybeAnyM.anyM(Maybe.empty());
    }


    @Override
    public <T, R> AnyM<maybe, R> flatMap(AnyM<maybe, T> t,
                                         Function<? super T, ? extends AnyM<maybe, ? extends R>> fn) {
        return MaybeAnyM.anyM(Maybes.fromPublisher(futureW(t).flatMap(fn.andThen(a -> futureW(a)))));

    }

    @Override
    public <T> AnyM<maybe, T> unitIterable(Iterable<T> it) {
        return MaybeAnyM.anyM(Maybes.fromPublisher(Future.fromIterable(it)));
    }

    @Override
    public <T> AnyM<maybe, T> unit(T o) {
        return MaybeAnyM.anyM(Maybe.just(o));
    }

    @Override
    public <T> ReactiveSeq<T> toStream(AnyM<maybe, T> t) {
        return ReactiveSeq.fromPublisher(future(t).toFlowable());
    }

    @Override
    public <T, R> AnyM<maybe, R> map(AnyM<maybe, T> t,
                                     Function<? super T, ? extends R> fn) {
        return MaybeAnyM.anyM(future(t).map(x -> fn.apply(x)));
    }
}
