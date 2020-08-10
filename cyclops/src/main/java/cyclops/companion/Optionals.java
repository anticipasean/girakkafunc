package cyclops.companion;

import cyclops.container.traversable.IterableX;
import cyclops.container.control.Either;
import cyclops.container.control.Maybe;
import cyclops.container.control.Option;
import cyclops.function.enhanced.Function3;
import cyclops.function.enhanced.Function4;
import cyclops.function.combiner.Monoid;
import cyclops.function.combiner.Reducer;
import cyclops.reactive.ReactiveSeq;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;


/**
 * Utility class for working with JDK Optionals
 *
 * @author johnmcclean
 */
@UtilityClass
public class Optionals {


    public <T> Optional<T> fromIterable(Iterable<? extends T> ita) {
        Iterator<? extends T> it = ita.iterator();
        if (it.hasNext()) {
            return Optional.of(it.next());
        }
        return Optional.empty();
    }

    public static <T, R> Optional<R> tailRec(T initial,
                                             Function<? super T, ? extends Optional<? extends Either<T, R>>> fn) {
        Optional<? extends Either<T, R>> next[] = new Optional[1];
        next[0] = Optional.of(Either.left(initial));
        boolean cont = true;
        do {
            cont = Optionals.fold(next[0],
                                  p -> p.fold(s -> {
                                                  next[0] = fn.apply(s);
                                                  return true;
                                              },
                                              pr -> false),
                                  () -> false);
        } while (cont);
        return next[0].map(x -> x.orElse(null));
    }


    public static <T, R> R fold(Optional<T> optional,
                                Function<? super T, ? extends R> fn,
                                Supplier<R> s) {
        return optional.isPresent() ? fn.apply(optional.get()) : s.get();
    }


    /**
     * Perform a For Comprehension over a Optional, accepting 3 generating function. This results in a four level nested internal
     * iteration over the provided Optionals.
     *
     * <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Optionals.forEach4;
     *
     * forEach4(Optional.just(1),
     * a-> Optional.just(a+1),
     * (a,b) -> Optional.<Integer>just(a+b),
     * a                  (a,b,c) -> Optional.<Integer>just(a+b+c),
     * Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1           top level Optional
     * @param value2           Nested Optional
     * @param value3           Nested Optional
     * @param value4           Nested Optional
     * @param yieldingFunction Generates a result per combination
     * @return Optional with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Optional<R> forEach4(Optional<? extends T1> value1,
                                                                   Function<? super T1, ? extends Optional<R1>> value2,
                                                                   BiFunction<? super T1, ? super R1, ? extends Optional<R2>> value3,
                                                                   Function3<? super T1, ? super R1, ? super R2, ? extends Optional<R3>> value4,
                                                                   Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Optional<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Optional<R2> b = value3.apply(in,
                                              ina);
                return b.flatMap(inb -> {
                    Optional<R3> c = value4.apply(in,
                                                  ina,
                                                  inb);
                    return c.map(in2 -> yieldingFunction.apply(in,
                                                               ina,
                                                               inb,
                                                               in2));
                });

            });

        });

    }

    /**
     * Perform a For Comprehension over a Optional, accepting 3 generating function. This results in a four level nested internal
     * iteration over the provided Optionals.
     *
     * <pre>
     * {@code
     *
     *  import static com.oath.cyclops.reactor.Optionals.forEach4;
     *
     *  forEach4(Optional.just(1),
     * a-> Optional.just(a+1),
     * (a,b) -> Optional.<Integer>just(a+b),
     * (a,b,c) -> Optional.<Integer>just(a+b+c),
     * (a,b,c,d) -> a+b+c+d <100,
     * Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1           top level Optional
     * @param value2           Nested Optional
     * @param value3           Nested Optional
     * @param value4           Nested Optional
     * @param filterFunction   A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Optional with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Optional<R> forEach4(Optional<? extends T1> value1,
                                                                   Function<? super T1, ? extends Optional<R1>> value2,
                                                                   BiFunction<? super T1, ? super R1, ? extends Optional<R2>> value3,
                                                                   Function3<? super T1, ? super R1, ? super R2, ? extends Optional<R3>> value4,
                                                                   Function4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                                   Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Optional<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Optional<R2> b = value3.apply(in,
                                              ina);
                return b.flatMap(inb -> {
                    Optional<R3> c = value4.apply(in,
                                                  ina,
                                                  inb);
                    return c.filter(in2 -> filterFunction.apply(in,
                                                                ina,
                                                                inb,
                                                                in2))
                            .map(in2 -> yieldingFunction.apply(in,
                                                               ina,
                                                               inb,
                                                               in2));
                });

            });

        });

    }

    /**
     * Perform a For Comprehension over a Optional, accepting 2 generating function. This results in a three level nested internal
     * iteration over the provided Optionals.
     *
     * <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Optionals.forEach3;
     *
     * forEach3(Optional.just(1),
     * a-> Optional.just(a+1),
     * (a,b) -> Optional.<Integer>just(a+b),
     * Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1           top level Optional
     * @param value2           Nested Optional
     * @param value3           Nested Optional
     * @param yieldingFunction Generates a result per combination
     * @return Optional with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Optional<R> forEach3(Optional<? extends T1> value1,
                                                           Function<? super T1, ? extends Optional<R1>> value2,
                                                           BiFunction<? super T1, ? super R1, ? extends Optional<R2>> value3,
                                                           Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Optional<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Optional<R2> b = value3.apply(in,
                                              ina);
                return b.map(in2 -> yieldingFunction.apply(in,
                                                           ina,
                                                           in2));
            });


        });

    }

    /**
     * Perform a For Comprehension over a Optional, accepting 2 generating function. This results in a three level nested internal
     * iteration over the provided Optionals.
     *
     * <pre>
     * {@code
     *
     *  import static com.oath.cyclops.reactor.Optionals.forEach3;
     *
     *  forEach3(Optional.just(1),
     * a-> Optional.just(a+1),
     * (a,b) -> Optional.<Integer>just(a+b),
     * (a,b,c) -> a+b+c <100,
     * Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1           top level Optional
     * @param value2           Nested Optional
     * @param value3           Nested Optional
     * @param filterFunction   A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Optional with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Optional<R> forEach3(Optional<? extends T1> value1,
                                                           Function<? super T1, ? extends Optional<R1>> value2,
                                                           BiFunction<? super T1, ? super R1, ? extends Optional<R2>> value3,
                                                           Function3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                                                           Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Optional<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Optional<R2> b = value3.apply(in,
                                              ina);
                return b.filter(in2 -> filterFunction.apply(in,
                                                            ina,
                                                            in2))
                        .map(in2 -> yieldingFunction.apply(in,
                                                           ina,
                                                           in2));
            });


        });

    }

    /**
     * Perform a For Comprehension over a Optional, accepting a generating function. This results in a two level nested internal
     * iteration over the provided Optionals.
     *
     * <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Optionals.forEach;
     *
     * forEach(Optional.just(1),
     * a-> Optional.just(a+1),
     * Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1           top level Optional
     * @param value2           Nested Optional
     * @param yieldingFunction Generates a result per combination
     * @return Optional with a combined value generated by the yielding function
     */
    public static <T, R1, R> Optional<R> forEach2(Optional<? extends T> value1,
                                                  Function<? super T, Optional<R1>> value2,
                                                  BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Optional<R1> a = value2.apply(in);
            return a.map(in2 -> yieldingFunction.apply(in,
                                                       in2));
        });


    }

    /**
     * Perform a For Comprehension over a Optional, accepting a generating function. This results in a two level nested internal
     * iteration over the provided Optionals.
     *
     * <pre>
     * {@code
     *
     *  import static com.oath.cyclops.reactor.Optionals.forEach;
     *
     *  forEach(Optional.just(1),
     * a-> Optional.just(a+1),
     * (a,b) -> Optional.<Integer>just(a+b),
     * (a,b,c) -> a+b+c <100,
     * Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1           top level Optional
     * @param value2           Nested Optional
     * @param filterFunction   A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Optional with a combined value generated by the yielding function
     */
    public static <T, R1, R> Optional<R> forEach2(Optional<? extends T> value1,
                                                  Function<? super T, ? extends Optional<R1>> value2,
                                                  BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                                  BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Optional<R1> a = value2.apply(in);
            return a.filter(in2 -> filterFunction.apply(in,
                                                        in2))
                    .map(in2 -> yieldingFunction.apply(in,
                                                       in2));
        });


    }


    public static Optional<Double> optional(OptionalDouble d) {
        return d.isPresent() ? Optional.of(d.getAsDouble()) : Optional.empty();
    }

    public static Optional<Long> optional(OptionalLong l) {
        return l.isPresent() ? Optional.of(l.getAsLong()) : Optional.empty();
    }

    public static Optional<Integer> optional(OptionalInt l) {
        return l.isPresent() ? Optional.of(l.getAsInt()) : Optional.empty();
    }

    /**
     * Sequence operation, take a Collection of Optionals and turn it into a Optional with a Collection By constrast with {@link
     * Optionals#sequencePresent(IterableX)}, if any Optionals are zero the result is an zero Optional
     *
     * <pre>
     * {@code
     *
     *  Optional<Integer> just = Optional.of(10);
     * Optional<Integer> none = Optional.zero();
     *
     *  Optional<Seq<Integer>> opts = Optionals.sequence(Seq.of(just, none, Optional.of(1)));
     * //Optional.zero();
     *
     * }
     * </pre>
     *
     * @param opts Maybes to Sequence
     * @return Maybe with a List of values
     */
    public static <T> Optional<ReactiveSeq<T>> sequence(final IterableX<? extends Optional<T>> opts) {
        return sequence(opts.stream());

    }

    /**
     * Sequence operation, take a Collection of Optionals and turn it into a Optional with a Collection Only successes are
     * retained. By constrast with {@link Optionals#sequence(IterableX)} Optional#zero types are tolerated and ignored.
     *
     * <pre>
     * {@code
     *  Optional<Integer> just = Optional.of(10);
     * Optional<Integer> none = Optional.zero();
     *
     * Optional<Seq<Integer>> maybes = Optionals.sequencePresent(Seq.of(just, none, Optional.of(1)));
     * //Optional.of(Seq.of(10, 1));
     * }
     * </pre>
     *
     * @param opts Optionals to Sequence
     * @return Optional with a List of values
     */
    public static <T> Optional<ReactiveSeq<T>> sequencePresent(final IterableX<? extends Optional<T>> opts) {
        return sequence(opts.stream()
                            .filter(Optional::isPresent));
    }

    /**
     * Sequence operation, take a Collection of Optionals and turn it into a Optional with a Collection By constrast with {@link
     * Optionals#sequencePresent(IterableX)} if any Optional types are zero the return type will be an zero Optional
     *
     * <pre>
     * {@code
     *
     *  Optional<Integer> just = Optional.of(10);
     * Optional<Integer> none = Optional.zero();
     *
     *  Optional<Seq<Integer>> maybes = Optionals.sequence(Seq.of(just, none, Optional.of(1)));
     * //Optional.zero();
     *
     * }
     * </pre>
     *
     * @param opts Maybes to Sequence
     * @return Optional with a List of values
     */
    public static <T> Optional<ReactiveSeq<T>> sequence(final Stream<? extends Optional<T>> opts) {
        return sequence(ReactiveSeq.fromStream(opts));

    }

    public static <T> Optional<ReactiveSeq<T>> sequence(ReactiveSeq<? extends Optional<T>> stream) {

        Optional<ReactiveSeq<T>> identity = Optional.of(ReactiveSeq.empty());

        BiFunction<Optional<ReactiveSeq<T>>, Optional<T>, Optional<ReactiveSeq<T>>> combineToStream = (acc, next) -> zip(acc,
                                                                                                                         next,
                                                                                                                         (a, b) -> a.append(b));

        BinaryOperator<Optional<ReactiveSeq<T>>> combineStreams = (a, b) -> zip(a,
                                                                                b,
                                                                                (z1, z2) -> z1.appendStream(z2));

        return stream.reduce(identity,
                             combineToStream,
                             combineStreams);
    }

    public static <T, R> Optional<ReactiveSeq<R>> traverse(Function<? super T, ? extends R> fn,
                                                           ReactiveSeq<Optional<T>> stream) {
        ReactiveSeq<Optional<R>> s = stream.map(h -> h.map(fn));
        return sequence(s);
    }

    /**
     * Accummulating operation using the supplied Reducer (@see cyclops2.Reducers). A typical use case is to accumulate into a
     * Persistent Collection type. Accumulates the present results, ignores zero Optionals.
     *
     * <pre>
     * {@code
     *  Optional<Integer> just = Optional.of(10);
     * Optional<Integer> none = Optional.zero();
     *
     * Optional<PersistentSetX<Integer>> opts = Optional.accumulateJust(Seq.of(just, none, Optional.of(1)), Reducers.toPersistentSetX());
     * //Optional.of(PersistentSetX.of(10, 1)));
     *
     * }
     * </pre>
     *
     * @param optionals Optionals to accumulate
     * @param reducer   Reducer to accumulate values with
     * @return Optional with reduced value
     */
    public static <T, R> Optional<R> accumulatePresent(final IterableX<Optional<T>> optionals,
                                                       final Reducer<R, T> reducer) {
        return sequencePresent(optionals).map(s -> s.foldMap(reducer));
    }

    /**
     * Accumulate the results only from those Optionals which have a value present, using the supplied mapping function to convert
     * the data from each Optional before reducing them using the supplied Monoid (a combining BiFunction/BinaryOperator and
     * identity element that takes two input values of the same type and returns the combined result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     *  Optional<Integer> just = Optional.of(10);
     * Optional<Integer> none = Optional.zero();
     *
     *  Optional<String> opts = Optional.accumulateJust(Seq.of(just, none, Optional.of(1)), i -> "" + i,
     * Monoids.stringConcat);
     * //Optional.of("101")
     *
     * }
     * </pre>
     *
     * @param optionals Optionals to accumulate
     * @param mapper    Mapping function to be applied to the result of each Optional
     * @param reducer   Monoid to combine values from each Optional
     * @return Optional with reduced value
     */
    public static <T, R> Optional<R> accumulatePresent(final IterableX<Optional<T>> optionals,
                                                       final Function<? super T, R> mapper,
                                                       final Monoid<R> reducer) {
        return sequencePresent(optionals).map(s -> s.map(mapper)
                                                    .reduce(reducer));
    }

    /**
     * Accumulate the results only from those Optionals which have a value present, using the supplied Monoid (a combining
     * BiFunction/BinaryOperator and identity element that takes two input values of the same type and returns the combined
     * result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     *  Optional<Integer> just = Optional.of(10);
     * Optional<Integer> none = Optional.zero();
     *
     *  Optional<String> opts = Optional.accumulateJust(Monoids.stringConcat,Seq.of(just, none, Optional.of(1)),
     * );
     * //Optional.of("101")
     *
     * }
     * </pre>
     *
     * @param optionals Optionals to accumulate
     * @param reducer   Monoid to combine values from each Optional
     * @return Optional with reduced value
     */
    public static <T> Optional<T> accumulatePresent(final Monoid<T> reducer,
                                                    final IterableX<Optional<T>> optionals) {
        return sequencePresent(optionals).map(s -> s.reduce(reducer));
    }

    /**
     * Combine an Optional with the provided Iterable (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  Optionals.zip(Optional.of(10),Arrays.asList(20), this::add)
     *  //Optional[30]
     *
     *  private int add(int a, int b) {
     * return a + b;
     * }
     *
     * }
     * </pre>
     *
     * @param f  Optional to combine with first element in Iterable (if present)
     * @param v  Iterable to combine
     * @param fn Combining function
     * @return Optional combined with supplied Iterable, or zero Optional if no value present
     */
    public static <T1, T2, R> Optional<R> zip(final Optional<? extends T1> f,
                                              final Iterable<? extends T2> v,
                                              final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(Option.fromOptional(f)
                            .zip(v,
                                 fn)
                            .toOptional());
    }

    public static <T1, T2, R> Optional<R> zip(final Optional<? extends T1> f,
                                              final Optional<? extends T2> v,
                                              final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(Option.fromOptional(f)
                            .zip(Option.fromOptional(v),
                                 fn)
                            .toOptional());
    }

    /**
     * Combine an Optional with the provided Publisher (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  Optionals.zip(Flux.just(10),Optional.of(10), this::add)
     *  //Optional[30]
     *
     *  private int add(int a, int b) {
     * return a + b;
     * }
     *
     * }
     * </pre>
     *
     * @param p  Publisher to combine
     * @param f  Optional to combine with
     * @param fn Combining function
     * @return Optional combined with supplied Publisher, or zero Optional if no value present
     */
    public static <T1, T2, R> Optional<R> zip(final Publisher<? extends T2> p,
                                              final Optional<? extends T1> f,
                                              final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(Maybe.fromOptional(f)
                           .zip(fn,
                                p)
                           .toOptional());
    }

    /**
     * Narrow covariant type parameter
     *
     * @param optional Optional with covariant type parameter
     * @return Narrowed Optional
     */
    public static <T> Optional<T> narrow(final Optional<? extends T> optional) {
        return (Optional<T>) optional;
    }


}
