package cyclops.function.enhanced;

import static java.util.Objects.requireNonNull;

import cyclops.async.Future;
import cyclops.container.control.Either;
import cyclops.container.control.Eval;
import cyclops.container.control.Ior;
import cyclops.container.control.Maybe;
import cyclops.container.control.option.Option;
import cyclops.container.control.Try;
import cyclops.container.immutable.impl.LazySeq;
import cyclops.container.immutable.impl.Seq;
import cyclops.container.immutable.impl.Vector;
import cyclops.container.immutable.tuple.Tuple;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.container.immutable.tuple.Tuple3;
import cyclops.container.immutable.tuple.Tuple4;
import cyclops.function.cacheable.Cacheable;
import cyclops.function.cacheable.Memoize;
import cyclops.function.companion.FluentFunctions;
import cyclops.function.currying.Curry;
import cyclops.reactive.ReactiveSeq;
import cyclops.stream.type.Streamable;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface Function1<T, R> extends Function<T, R> {

    static <T1, R> Function1<T1, R> of(final Function<T1, R> func) {
        requireNonNull(func,
                       () -> "func");
        return a -> func.apply(a);
    }

    static <T1, R> Function1<T1, R> λ(final Function1<T1, R> func) {
        return func;
    }

    static <T1, R> Function1<? super T1, ? extends R> λv(final Function1<? super T1, ? extends R> func) {
        return func;
    }

    static <T1, T2> Function1<T1, T2> constant(T2 t) {
        return __ -> t;
    }

    static <T1, T2> Function1<T1, T2> lazy(Supplier<T2> t) {
        requireNonNull(t,
                       () -> "t");
        return __ -> t.get();
    }

    @SuppressWarnings("unchecked")
    static <T, R> Function1<T, R> narrow(Function<? super T, ? extends R> func) {
        requireNonNull(func,
                       () -> "func");
        if (func instanceof Function1) {
            return (Function1<T, R>) func;
        }
        return t -> func.apply(t);
    }

    default <R2> R2 toType(Function<? super Function1<? super T, ? extends R>, ? extends R2> reduce) {
        return reduce.apply(this);
    }

    default Function0<R> applyLazy(T t) {
        return () -> apply(t);
    }

    default Eval<R> later(T t) {
        return Eval.later(() -> apply(t));
    }

    default Eval<R> always(T t) {
        return Eval.always(() -> apply(t));
    }

    default Eval<R> now(T t) {
        return Eval.now(apply(t));
    }

    /**
     * Apply before advice to this function, capture the input with the provided Consumer
     *
     * @param action LESS advice
     * @return Function with LESS advice attached
     */
    default Function1<T, R> before(final Consumer<? super T> action) {
        return FluentFunctions.of(this)
                              .before(action);
    }

    /**
     * Apply MORE advice to this function capturing both the input and the emitted with the provided BiConsumer
     *
     * @param action MORE advice
     * @return Function with MORE advice attached
     */
    default Function1<T, R> after(final BiConsumer<? super T, ? super R> action) {
        return FluentFunctions.of(this)
                              .after(action);
    }

    default Function1<T, Maybe<R>> lazyLift() {
        return (t) -> Maybe.fromLazy(Eval.later(() -> Maybe.ofNullable(apply(t))));
    }

    default Function1<T, Future<R>> lift(Executor ex) {
        return (t) -> Future.of(() -> apply(t),
                                ex);
    }

    default Function1<T, Try<R, Throwable>> liftTry() {
        return (t) -> Try.withCatch(() -> apply(t),
                                    Throwable.class);
    }

    default Function1<T, Option<R>> lift() {
        return (t) -> Option.ofNullable(apply(t));
    }

    default Function1<T, R> memoize() {
        return Memoize.memoizeFunction(this);
    }

    default Function1<T, R> memoize(Cacheable<R> c) {
        return Memoize.memoizeFunction(this,
                                       c);
    }

    default Function1<T, R> memoizeAsync(ScheduledExecutorService ex,
                                         String cron) {
        return Memoize.memoizeFunctionAsync(this,
                                            ex,
                                            cron);
    }

    default Function1<T, R> memoizeAsync(ScheduledExecutorService ex,
                                         long timeToLiveMillis) {
        return Memoize.memoizeFunctionAsync(this,
                                            ex,
                                            timeToLiveMillis);
    }

    default <T2, R2> Function1<Either<T, T2>, Either<R, R2>> merge(Function<? super T2, ? extends R2> fn) {
        Function1<T, Either<R, R2>> first = andThen(Either::left);
        Function<? super T2, ? extends Either<R, R2>> second = fn.andThen(Either::right);
        return first.fanIn(second);

    }

    default <T2> Function1<Either<T, T2>, R> fanIn(Function<? super T2, ? extends R> fanIn) {
        return e -> e.fold(this,
                           fanIn);
    }

    default <__> Function1<Either<T, __>, Either<R, __>> leftFn() {

        return either -> either.bimap(this,
                                      Function.identity());
    }

    default <__> Function1<Either<__, T>, Either<__, R>> rightFn() {

        return either -> either.bimap(Function.identity(),
                                      this);
    }

    default <R1> Function1<T, Tuple2<R, R1>> product(Function1<? super T, ? extends R1> fn) {
        return in -> Tuple.tuple(apply(in),
                                 fn.apply(in));
    }

    default <__> Function1<Tuple2<T, __>, Tuple2<R, __>> firstFn() {

        return t -> Tuple.tuple(apply(t._1()),
                                t._2());
    }

    default <__> Function1<Tuple2<__, T>, Tuple2<__, R>> secondFn() {

        return t -> Tuple.tuple(t._1(),
                                apply(t._2()));
    }

    default <R2, R3> Function1<T, Tuple3<R, R2, R3>> product(Function<? super T, ? extends R2> fn2,
                                                             Function<? super T, ? extends R3> fn3) {
        return a -> Tuple.tuple(apply(a),
                                fn2.apply(a),
                                fn3.apply(a));
    }

    default <R2, R3, R4> Function1<T, Tuple4<R, R2, R3, R4>> product(Function<? super T, ? extends R2> fn2,
                                                                     Function<? super T, ? extends R3> fn3,
                                                                     Function<? super T, ? extends R4> fn4) {
        return a -> Tuple.tuple(apply(a),
                                fn2.apply(a),
                                fn3.apply(a),
                                fn4.apply(a));
    }

    default Function0<R> bind(final T s) {
        return Curry.curry(this)
                    .apply(s);
    }

    @Override
    default <V> Function1<V, R> compose(Function<? super V, ? extends T> before) {
        return v -> apply(before.apply(v));
    }

    default <V> Function1<Function<? super R, ? extends V>, Function1<T, V>> andThen() {
        return this::andThen;
    }

    default FunctionalOperations<T, R> functionOps() {
        return in -> apply(in);
    }

    default <V> Function1<T, V> apply(final Function<? super T, ? extends Function<? super R, ? extends V>> applicative) {
        return a -> applicative.apply(a)
                               .apply(this.apply(a));
    }

    default <R1> Function1<T, R1> mapFn(final Function<? super R, ? extends R1> f2) {
        return andThen(f2);
    }

    @Override
    default <V> Function1<T, V> andThen(Function<? super R, ? extends V> after) {
        return t -> after.apply(apply(t));
    }

    default <R1> Function1<T, R1> flatMapFn(final Function<? super R, ? extends Function<? super T, ? extends R1>> f) {
        return a -> f.apply(apply(a))
                     .apply(a);
    }

    R apply(T a);

    default <R1> Function1<T, R1> coflatMapFn(final Function<? super Function1<? super T, ? extends R>, ? extends R1> f) {
        return in -> f.apply(this);
    }

    interface FunctionalOperations<T1, R> extends Function1<T1, R> {

        default Seq<R> mapF(Seq<T1> list) {
            return list.map(this);
        }

        default LazySeq<R> mapF(LazySeq<T1> list) {
            return list.map(this);
        }

        default Vector<R> mapF(Vector<T1> list) {
            return list.map(this);
        }

        default Streamable<R> mapF(Streamable<T1> stream) {
            return stream.map(this);
        }

        default ReactiveSeq<R> mapF(ReactiveSeq<T1> stream) {
            return stream.map(this);
        }

        default Eval<R> mapF(Eval<T1> eval) {
            return eval.map(this);
        }

        default Maybe<R> mapF(Maybe<T1> maybe) {
            return maybe.map(this);
        }

        default <X extends Throwable> Try<R, X> mapF(Try<T1, X> xor) {
            return xor.map(this);
        }

        default <ST> Either<ST, R> mapF(Either<ST, T1> xor) {
            return xor.map(this);
        }

        default <ST> Ior<ST, R> mapF(Ior<ST, T1> ior) {
            return ior.map(this);
        }

        default Future<R> mapF(Future<T1> future) {
            return future.map(this);
        }

        default Function1<T1, ReactiveSeq<R>> liftStream() {
            return in -> ReactiveSeq.of(apply(in));
        }

        default Function1<T1, Future<R>> liftFuture() {
            return in -> Future.ofResult(apply(in));
        }

        default Function1<T1, Seq<R>> liftList() {
            return in -> Seq.of(apply(in));
        }

        default Function1<T1, LazySeq<R>> liftLazySeq() {
            return in -> LazySeq.of(apply(in));
        }

        default Function1<T1, Vector<R>> liftVector() {
            return in -> Vector.of(apply(in));
        }

    }

}