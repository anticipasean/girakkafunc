package cyclops.reactor.container;


import cyclops.async.Future;
import cyclops.container.MonadicValue;
import cyclops.container.control.Option;
import cyclops.container.filterable.Filterable;
import cyclops.container.foldable.Foldable;
import cyclops.container.immutable.tuple.Tuple;
import cyclops.container.transformable.ReactiveTransformable;
import cyclops.container.transformable.To;
import cyclops.function.enhanced.Function3;
import cyclops.function.enhanced.Function4;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.reactive.ReactiveSeq;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import reactor.core.publisher.Mono;

/**
 * Monad Transformer for Mono's nested within another monadic type
 * <p>
 * <p>
 * MonoT allows the deeply wrapped Mono to be manipulating within it's nested /contained context
 *
 * @param <T> Type of data stored inside the nested Mono(s)
 * @author johnmcclean
 */
public final class MonoT<W extends WitnessType<W>, T> implements To<MonoT<W, T>>, ReactiveTransformable<T>, Filterable<T>,
                                                                 Foldable<T> {

    private final AnyM<W, Mono<T>> run;


    private MonoT(final AnyM<W, Mono<T>> run) {
        this.run = run;
    }

    private static <W extends WitnessType<W>, B> AnyM<W, Mono<B>> narrow(final AnyM<W, Mono<? extends B>> run) {
        return (AnyM) run;
    }

    public static <W extends WitnessType<W>, U, R> Function<MonoT<W, U>, MonoT<W, R>> lift(final Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    public static <W extends WitnessType<W>, U1, U2, R> BiFunction<MonoT<W, U1>, MonoT<W, U2>, MonoT<W, R>> lift2(final BiFunction<? super U1, ? super U2, ? extends R> fn) {
        return (optTu1, optTu2) -> optTu1.flatMapT(input1 -> optTu2.map(input2 -> fn.apply(input1,
                                                                                           input2)));
    }

    /**
     * Construct an MonoT from an AnyM that contains a monad type that contains type other than Mono The values in the underlying
     * monad will be mapped to Mono<A>
     *
     * @param anyM AnyM that doesn't contain a monad wrapping an Mono
     * @return MonoT
     */
    public static <W extends WitnessType<W>, A> MonoT<W, A> fromAnyM(final AnyM<W, A> anyM) {
        return of(anyM.map(Mono::just));
    }

    /**
     * Construct an MonoT from an AnyM that wraps a monad containing  MonoWs
     *
     * @param monads AnyM that contains a monad wrapping an Mono
     * @return MonoT
     */
    public static <W extends WitnessType<W>, A> MonoT<W, A> of(final AnyM<W, Mono<A>> monads) {
        return new MonoT<>(monads);
    }

    public Iterator<T> iterator() {
        return stream().iterator();
    }

    public ReactiveSeq<T> stream() {
        return run.stream()
                  .map(Mono::block);
    }

    public Option<T> get() {
        return stream().takeOne();
    }

    public T orElse(T value) {
        return stream().findAny()
                       .orElse(value);
    }

    public T orElseGet(Supplier<? super T> s) {
        return stream().findAny()
                       .orElseGet((Supplier<T>) s);
    }

    /**
     * @return The wrapped AnyM
     */
    public AnyM<W, Mono<T>> unwrap() {
        return run;
    }

    public <R> R unwrapTo(Function<? super AnyM<W, Mono<T>>, ? extends R> fn) {
        return unwrap().to(fn);
    }

    public AnyM<W, ? extends MonadicValue<T>> transformerStream() {

        return run.map(m -> Future.fromPublisher(m));
    }

    @Override
    public MonoT<W, T> filter(final Predicate<? super T> test) {
        return of(run.map(f -> f.map(in -> Tuple.tuple(in,
                                                       test.test(in))))
                     .filter(f -> f.block()
                                   ._2())
                     .map(f -> f.map(in -> in._1())));
    }

    /**
     * Peek at the current value of the Mono
     * <pre>
     * {@code
     *    MonoT.of(AnyM.fromStream(Arrays.asMonoW(10))
     *             .peek(System.out::println);
     *
     *     //prints 10
     * }
     * </pre>
     *
     * @param peek Consumer to accept current value of Mono
     * @return MonoT with peek call
     */
    @Override
    public MonoT<W, T> peek(final Consumer<? super T> peek) {
        return map(e -> {
            peek.accept(e);
            return e;
        });

    }

    /**
     * Map the wrapped Mono
     *
     * <pre>
     * {@code
     *  MonoT.of(AnyM.fromStream(Arrays.asMonoW(10))
     *             .map(t->t=t+1);
     *
     *
     *  //MonoT<AnyMSeq<Stream<Mono[11]>>>
     * }
     * </pre>
     *
     * @param f Mapping function for the wrapped Mono
     * @return MonoT that applies the map function to the wrapped Mono
     */
    @Override
    public <B> MonoT<W, B> map(final Function<? super T, ? extends B> f) {
        return new MonoT<W, B>(run.map(o -> o.map(f)));
    }

    /**
     * Flat Map the wrapped Mono
     *
     * @param f FlatMap function
     * @return MonoT that applies the flatMap function to the wrapped Mono
     */

    public <B> MonoT<W, B> flatMapT(final Function<? super T, MonoT<W, B>> f) {
        MonoT<W, B> r = of(run.map(future -> Mono.from(future.flatMap(a -> {
            Mono<B> m = f.apply(a).run.stream()
                                      .toList()
                                      .get(0);
            return m;
        }))));
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("MonoT[%s]",
                             run.unwrap()
                                .toString());
    }


    public <R> MonoT<W, R> unitIterable(final Iterable<R> it) {
        return of(run.unitIterable(it)
                     .map(i -> Mono.just(i)));
    }


    @Override
    public int hashCode() {
        return run.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof MonoT) {
            return run.equals(((MonoT) o).run);
        }
        return false;
    }


    public <T2, R1, R2, R3, R> MonoT<W, R> forEach4M(Function<? super T, ? extends MonoT<W, R1>> value1,
                                                     BiFunction<? super T, ? super R1, ? extends MonoT<W, R2>> value2,
                                                     Function3<? super T, ? super R1, ? super R2, ? extends MonoT<W, R3>> value3,
                                                     Function4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {
        return this.flatMapT(in -> value1.apply(in)
                                         .flatMapT(in2 -> value2.apply(in,
                                                                       in2)
                                                                .flatMapT(in3 -> value3.apply(in,
                                                                                              in2,
                                                                                              in3)
                                                                                       .map(in4 -> yieldingFunction.apply(in,
                                                                                                                          in2,
                                                                                                                          in3,
                                                                                                                          in4)))));

    }

    public <T2, R1, R2, R3, R> MonoT<W, R> forEach4M(Function<? super T, ? extends MonoT<W, R1>> value1,
                                                     BiFunction<? super T, ? super R1, ? extends MonoT<W, R2>> value2,
                                                     Function3<? super T, ? super R1, ? super R2, ? extends MonoT<W, R3>> value3,
                                                     Function4<? super T, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                     Function4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {
        return this.flatMapT(in -> value1.apply(in)
                                         .flatMapT(in2 -> value2.apply(in,
                                                                       in2)
                                                                .flatMapT(in3 -> value3.apply(in,
                                                                                              in2,
                                                                                              in3)
                                                                                       .filter(in4 -> filterFunction.apply(in,
                                                                                                                           in2,
                                                                                                                           in3,
                                                                                                                           in4))
                                                                                       .map(in4 -> yieldingFunction.apply(in,
                                                                                                                          in2,
                                                                                                                          in3,
                                                                                                                          in4)))));

    }

    public <T2, R1, R2, R> MonoT<W, R> forEach3M(Function<? super T, ? extends MonoT<W, R1>> value1,
                                                 BiFunction<? super T, ? super R1, ? extends MonoT<W, R2>> value2,
                                                 Function3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return this.flatMapT(in -> value1.apply(in)
                                         .flatMapT(in2 -> value2.apply(in,
                                                                       in2)
                                                                .map(in3 -> yieldingFunction.apply(in,
                                                                                                   in2,
                                                                                                   in3))));

    }

    public <T2, R1, R2, R> MonoT<W, R> forEach3M(Function<? super T, ? extends MonoT<W, R1>> value1,
                                                 BiFunction<? super T, ? super R1, ? extends MonoT<W, R2>> value2,
                                                 Function3<? super T, ? super R1, ? super R2, Boolean> filterFunction,
                                                 Function3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return this.flatMapT(in -> value1.apply(in)
                                         .flatMapT(in2 -> value2.apply(in,
                                                                       in2)
                                                                .filter(in3 -> filterFunction.apply(in,
                                                                                                    in2,
                                                                                                    in3))
                                                                .map(in3 -> yieldingFunction.apply(in,
                                                                                                   in2,
                                                                                                   in3))));

    }

    public <R1, R> MonoT<W, R> forEach2M(Function<? super T, ? extends MonoT<W, R1>> value1,
                                         BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return this.flatMapT(in -> value1.apply(in)
                                         .map(in2 -> yieldingFunction.apply(in,
                                                                            in2)));
    }

    public <R1, R> MonoT<W, R> forEach2M(Function<? super T, ? extends MonoT<W, R1>> value1,
                                         BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                         BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return this.flatMapT(in -> value1.apply(in)
                                         .filter(in2 -> filterFunction.apply(in,
                                                                             in2))
                                         .map(in2 -> yieldingFunction.apply(in,
                                                                            in2)));
    }

    public String mkString() {
        return toString();
    }


    @Override
    public <U> MonoT<W, U> ofType(Class<? extends U> type) {
        return (MonoT<W, U>) Filterable.super.ofType(type);
    }

    @Override
    public MonoT<W, T> filterNot(Predicate<? super T> predicate) {
        return (MonoT<W, T>) Filterable.super.filterNot(predicate);
    }

    @Override
    public MonoT<W, T> notNull() {
        return (MonoT<W, T>) Filterable.super.notNull();
    }


    @Override
    public <R> MonoT<W, R> retry(Function<? super T, ? extends R> fn) {
        return (MonoT<W, R>) ReactiveTransformable.super.retry(fn);
    }

    @Override
    public <R> MonoT<W, R> retry(Function<? super T, ? extends R> fn,
                                 int retries,
                                 long delay,
                                 TimeUnit timeUnit) {
        return (MonoT<W, R>) ReactiveTransformable.super.retry(fn,
                                                               retries,
                                                               delay,
                                                               timeUnit);
    }


}
