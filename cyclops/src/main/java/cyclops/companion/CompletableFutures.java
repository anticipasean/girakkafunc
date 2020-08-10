package cyclops.companion;

import cyclops.function.hkt.DataWitness.future;
import cyclops.function.hkt.Higher;
import cyclops.container.traversable.IterableX;
import cyclops.control.Either;
import cyclops.async.Future;
import cyclops.function.Function3;
import cyclops.function.Function4;
import cyclops.function.combiner.Monoid;
import cyclops.function.combiner.Reducer;
import cyclops.reactive.ReactiveSeq;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;

/**
 * Utilty methods for working with JDK CompletableFutures
 *
 * @author johnmcclean
 */
@UtilityClass
public class CompletableFutures {

    public static <T> CompletableFuture<T> error(Throwable t) {
        CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(t);
        return cf;
    }

    public static <T, R> CompletableFuture<R> tailRec(T initial,
                                                      Function<? super T, ? extends CompletableFuture<? extends Either<T, R>>> fn) {
        Higher<future, R> x = Future.tailRec(initial,
                                             fn.andThen(Future::of));
        return Future.narrowK(x)
                     .getFuture();
    }


    public static <T> CompletableFuture<T> ofResult(T value) {
        CompletableFuture<T> result = new CompletableFuture<T>();
        result.complete(value);
        return result;
    }


    /**
     * Perform a For Comprehension over a CompletableFuture, accepting 3 generating function. This results in a four level nested
     * internal iteration over the provided CompletableFutures.
     *
     * <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.CompletableFutures.forEach4;
     *
     * forEach4(CompletableFuture.just(1),
     * a-> CompletableFuture.just(a+1),
     * (a,b) -> CompletableFuture.<Integer>just(a+b),
     * a                  (a,b,c) -> CompletableFuture.<Integer>just(a+b+c),
     * Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1           top level CompletableFuture
     * @param value2           Nested CompletableFuture
     * @param value3           Nested CompletableFuture
     * @param value4           Nested CompletableFuture
     * @param yieldingFunction Generates a result per combination
     * @return CompletableFuture with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> CompletableFuture<R> forEach4(CompletableFuture<? extends T1> value1,
                                                                            Function<? super T1, ? extends CompletableFuture<R1>> value2,
                                                                            BiFunction<? super T1, ? super R1, ? extends CompletableFuture<R2>> value3,
                                                                            Function3<? super T1, ? super R1, ? super R2, ? extends CompletableFuture<R3>> value4,
                                                                            Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.thenCompose(in -> {

            CompletableFuture<R1> a = value2.apply(in);
            return a.thenCompose(ina -> {
                CompletableFuture<R2> b = value3.apply(in,
                                                       ina);
                return b.thenCompose(inb -> {
                    CompletableFuture<R3> c = value4.apply(in,
                                                           ina,
                                                           inb);
                    return c.thenApply(in2 -> yieldingFunction.apply(in,
                                                                     ina,
                                                                     inb,
                                                                     in2));
                });

            });

        });

    }


    /**
     * Perform a For Comprehension over a CompletableFuture, accepting 2 generating function. This results in a three level nested
     * internal iteration over the provided CompletableFutures.
     *
     * <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.CompletableFutures.forEach3;
     *
     * forEach3(CompletableFuture.just(1),
     * a-> CompletableFuture.just(a+1),
     * (a,b) -> CompletableFuture.<Integer>just(a+b),
     * Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1           top level CompletableFuture
     * @param value2           Nested CompletableFuture
     * @param value3           Nested CompletableFuture
     * @param yieldingFunction Generates a result per combination
     * @return CompletableFuture with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> CompletableFuture<R> forEach3(CompletableFuture<? extends T1> value1,
                                                                    Function<? super T1, ? extends CompletableFuture<R1>> value2,
                                                                    BiFunction<? super T1, ? super R1, ? extends CompletableFuture<R2>> value3,
                                                                    Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.thenCompose(in -> {

            CompletableFuture<R1> a = value2.apply(in);
            return a.thenCompose(ina -> {
                CompletableFuture<R2> b = value3.apply(in,
                                                       ina);

                return b.thenApply(in2 -> yieldingFunction.apply(in,
                                                                 ina,
                                                                 in2));


            });

        });

    }


    /**
     * Perform a For Comprehension over a CompletableFuture, accepting a generating function. This results in a two level nested
     * internal iteration over the provided CompletableFutures.
     *
     * <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.CompletableFutures.forEach;
     *
     * forEach(CompletableFuture.just(1),
     * a-> CompletableFuture.just(a+1),
     * Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1           top level CompletableFuture
     * @param value2           Nested CompletableFuture
     * @param yieldingFunction Generates a result per combination
     * @return CompletableFuture with a combined value generated by the yielding function
     */
    public static <T, R1, R> CompletableFuture<R> forEach2(CompletableFuture<? extends T> value1,
                                                           Function<? super T, CompletableFuture<R1>> value2,
                                                           BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.thenCompose(in -> {

            CompletableFuture<R1> a = value2.apply(in);
            return a.thenApply(ina -> yieldingFunction.apply(in,
                                                             ina));


        });


    }


    /**
     * Asynchronous sequence operation that convert a Collection of Futures to a Future with a List
     *
     * <pre>
     * {@code
     *   CompletableFuture<Seq<Integer>> futures =CompletableFuture.sequence(Seq.of(
     *                                                          CompletableFuture.completedFuture(10),
     *                                                          CompletableFuture.completedFuture(1)));
     * //Seq.of(10,1)
     *
     * }
     * </pre>
     *
     * @param fts Collection of Futures to Sequence into a Future with a List
     * @return Future with a List
     */
    public static <T> CompletableFuture<ReactiveSeq<T>> sequence(final Iterable<? extends CompletableFuture<T>> fts) {
        return sequence(ReactiveSeq.fromIterable(fts));
    }

    /**
     * Asynchronous sequence operation that convert a Stream of Futures to a Future with a Stream
     *
     * <pre>
     * {@code
     *   CompletableFuture<Seq<Integer>> futures =CompletableFuture.sequence(Seq.of(
     *                                                          CompletableFuture.completedFuture(10),
     *                                                          CompletableFuture.completedFuture(1)));
     * //Seq.of(10,1)
     *
     * }
     * </pre>
     *
     * @param fts Stream of Futures to Sequence into a Future with a Stream
     * @return Future with a Stream
     */
    public static <T> CompletableFuture<ReactiveSeq<T>> sequence(final Stream<? extends CompletableFuture<T>> fts) {
        return sequence(ReactiveSeq.fromStream((fts)));

    }

    public static <T> CompletableFuture<ReactiveSeq<T>> sequence(ReactiveSeq<? extends CompletableFuture<T>> stream) {

        CompletableFuture<ReactiveSeq<T>> identity = CompletableFuture.completedFuture(ReactiveSeq.empty());

        BiFunction<CompletableFuture<ReactiveSeq<T>>, CompletableFuture<T>, CompletableFuture<ReactiveSeq<T>>> combineToStream = (acc, next) -> acc.thenCombine(next,
                                                                                                                                                                (a, b) -> a.append(b));

        BinaryOperator<CompletableFuture<ReactiveSeq<T>>> combineStreams = (a, b) -> a.thenCombine(b,
                                                                                                   (z1, z2) -> z1.appendStream(z2));

        return stream.reduce(identity,
                             combineToStream,
                             combineStreams);
    }

    public static <T, R> CompletableFuture<ReactiveSeq<R>> traverse(Function<? super T, ? extends R> fn,
                                                                    ReactiveSeq<CompletableFuture<T>> stream) {
        ReactiveSeq<CompletableFuture<R>> s = stream.map(h -> h.thenApply(fn));
        return sequence(s);
    }

    /**
     * Asynchronously accumulate the results only from those Futures which have completed successfully. Also @see {@link
     * CompletableFutures#accumulate(IterableX, Reducer)} if you would like a failure to result in a CompletableFuture with an
     * error
     * <pre>
     * {@code
     *
     * CompletableFuture<Integer> just = CompletableFuture.completedFuture(10);
     * CompletableFuture<Integer> none = Future.ofError(new NoSuchElementException())
     * .getFuture();
     *
     * CompletableFuture<PersistentSetX<Integer>> futures = CompletableFutures.accumulateSuccess(Seq.of(just,none,CompletableFuture.completedFuture(1)),Reducers.toPersistentSetX());
     *
     * //CompletableFuture[PersistentSetX[10,1]]
     *  }
     *  </pre>
     *
     * @param fts     Collection of Futures to accumulate successes
     * @param reducer Reducer to accumulate results
     * @return CompletableFuture asynchronously populated with the accumulate success operation
     */
    public static <T, R> CompletableFuture<R> accumulateSuccess(final Iterable<CompletableFuture<T>> fts,
                                                                final Reducer<R, T> reducer) {
        CompletableFuture<R> result = new CompletableFuture<>();
        Stream<T> successes = ReactiveSeq.fromIterable(fts)
                                         .filter(ft -> !ft.isCompletedExceptionally())
                                         .map(CompletableFuture::join);
        CompletableFuture.allOf(ReactiveSeq.fromIterable(fts)
                                           .toArray(i -> new CompletableFuture[i]))
                         .thenRun(() -> result.complete(reducer.foldMap(successes)))
                         .exceptionally(e -> {
                             result.complete(reducer.foldMap(successes));
                             return null;
                         });

        return result;
    }

    /**
     * Asynchronously accumulate the results only from those Futures which have completed successfully, using the supplied mapping
     * function to convert the data from each Future before reducing them using the supplied Monoid (a combining
     * BiFunction/BinaryOperator and identity element that takes two input values of the same type and returns the combined
     * result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     * CompletableFuture<String> future = CompletableFutures.accumulate(Seq.of(CompletableFuture.completedFuture(10),CompletableFuture.completedFuture(1)),i->""+i,Monoids.stringConcat);
     * //CompletableFuture["101"]
     * }
     * </pre>
     *
     * @param fts     Collection of Futures to accumulate successes
     * @param mapper  Mapping function to be applied to the result of each Future
     * @param reducer Monoid to combine values from each Future
     * @return CompletableFuture asynchronously populated with the accumulate operation
     */
    public static <T, R> CompletableFuture<R> accumulateSuccess(final Iterable<CompletableFuture<T>> fts,
                                                                final Function<? super T, R> mapper,
                                                                final Monoid<R> reducer) {
        CompletableFuture<R> result = new CompletableFuture<>();
        ReactiveSeq<R> successes = ReactiveSeq.fromIterable(fts)
                                              .filter(ft -> !ft.isCompletedExceptionally())
                                              .map(CompletableFuture::join)
                                              .map(mapper);
        CompletableFuture.allOf(ReactiveSeq.fromIterable(fts)
                                           .toArray(i -> new CompletableFuture[i]))
                         .thenRun(() -> result.complete(successes.reduce(reducer)))
                         .exceptionally(e -> {
                             result.complete(successes.reduce(reducer));
                             return null;
                         });

        return result;
    }

    /**
     * Asynchronously accumulate the results only from those Futures which have completed successfully, reducing them using the
     * supplied Monoid (a combining BiFunction/BinaryOperator and identity element that takes two input values of the same type
     * and returns the combined result) {@see cyclops2.Monoids }
     *
     * <pre>
     * {@code
     * CompletableFuture<Integer> just =CompletableFuture.completedFuture(10);
     * CompletableFuture<Integer> future =CompletableFutures.accumulate(Monoids.intSum, Seq.of(just,CompletableFuture.completedFuture(1)));
     * //CompletableFuture[11]
     * }
     * </pre>
     *
     * @param fts     Collection of Futures to accumulate successes
     * @param reducer Monoid to combine values from each Future
     * @return CompletableFuture asynchronously populated with the accumulate operation
     */
    public static <T, R> CompletableFuture<T> accumulateSuccess(final Monoid<T> reducer,
                                                                final Iterable<CompletableFuture<T>> fts) {
        CompletableFuture<T> result = new CompletableFuture<>();
        ReactiveSeq<T> successes = ReactiveSeq.fromIterable(fts)
                                              .filter(ft -> !ft.isCompletedExceptionally())
                                              .map(CompletableFuture::join);
        CompletableFuture.allOf(ReactiveSeq.fromIterable(fts)
                                           .toArray(i -> new CompletableFuture[i]))
                         .thenRun(() -> result.complete(successes.reduce(reducer)))
                         .exceptionally(e -> {
                             result.complete(successes.reduce(reducer));
                             return null;
                         });

        return result;
    }

    /**
     * Asynchronously accumulate the results of Futures, a single failure will cause a failed result, using the supplied Reducer
     * {@see cyclops2.Reducers}
     * <pre>
     * {@code
     *
     * CompletableFuture<Integer> just =CompletableFuture.completedFuture(10);
     * CompletableFuture<Integer> none = Future.ofError(new NoSuchElementException()).getFuture();
     *
     * CompletableFuture<PersistentSetX<Integer>> futures = CompletableFutures.accumulateSuccess(Seq.of(just,none,CompletableFuture.completedFuture(1)),Reducers.toPersistentSetX());
     *
     * //CompletableFuture[PersistentSetX[10,1]]
     *  }
     *  </pre>
     *
     * @param fts     Collection of Futures to accumulate successes
     * @param reducer Reducer to accumulate results
     * @return Future asynchronously populated with the accumulate success operation
     */
    public static <T, R> CompletableFuture<R> accumulate(final IterableX<CompletableFuture<T>> fts,
                                                         final Reducer<R, T> reducer) {
        return sequence(fts).thenApply(s -> s.foldMap(reducer));
    }

    /**
     * Asynchronously accumulate the results of a batch of Futures which using the supplied mapping function to convert the data
     * from each Future before reducing them using the supplied supplied Monoid (a combining BiFunction/BinaryOperator and
     * identity element that takes two input values of the same type and returns the combined result) {@see cyclops2.Monoids }. A
     * single Failure results in a Failed  Future.
     *
     * <pre>
     * {@code
     * CompletableFuture<String> future = Future.accumulate(Seq.of(CompletableFuture.completedFuture(10),CompletableFuture.completedFuture(1)),i->""+i,Monoids.stringConcat);
     * //CompletableFuture["101"]
     * }
     * </pre>
     *
     * @param fts     Collection of Futures to accumulate successes
     * @param mapper  Mapping function to be applied to the result of each Future
     * @param reducer Monoid to combine values from each Future
     * @return CompletableFuture asynchronously populated with the accumulate operation
     */
    public static <T, R> CompletableFuture<R> accumulate(final IterableX<CompletableFuture<T>> fts,
                                                         final Function<? super T, R> mapper,
                                                         final Monoid<R> reducer) {
        return sequence(fts).thenApply(s -> s.map(mapper)
                                             .reduce(reducer));
    }

    /**
     * Asynchronously accumulate the results only from the provided Futures, reducing them using the supplied Monoid (a combining
     * BiFunction/BinaryOperator and identity element that takes two input values of the same type and returns the combined
     * result) {@see cyclops2.Monoids }.
     * <p>
     * A single Failure results in a Failed  Future.
     *
     * <pre>
     * {@code
     * CompletableFuture<Integer> just =CompletableFuture.completedFuture(10);
     *
     * CompletableFuture<Integer> future =CompletableFutures.accumulate(Monoids.intSum,Seq.of(just,CompletableFuture.completableFuture(1)));
     * //CompletableFuture[11]
     * }
     * </pre>
     *
     * @param fts     Collection of Futures to accumulate successes
     * @param reducer Monoid to combine values from each Future
     * @return CompletableFuture asynchronously populated with the accumulate operation
     */
    public static <T> CompletableFuture<T> accumulate(final Monoid<T> reducer,
                                                      final IterableX<CompletableFuture<T>> fts) {
        return sequence(fts).thenApply(s -> s.reduce(reducer));
    }

    /**
     * Schedule the population of a CompletableFuture from the provided Supplier, the provided Cron (Quartz format) expression
     * will be used to trigger the population of the CompletableFuture. The provided ScheduledExecutorService provided the thread
     * on which the Supplier will be executed.
     *
     * <pre>
     * {@code
     *
     *    CompletableFuture<String> future = CompletableFutures.schedule("* * * * * ?", Executors.newScheduledThreadPool(1), ()->"hello");
     *
     *    //CompletableFuture["hello"]
     *
     * }</pre>
     *
     * @param cron Cron expression in Quartz format
     * @param ex   ScheduledExecutorService used to execute the provided Supplier
     * @param t    The Supplier to execute to populate the CompletableFuture
     * @return CompletableFuture populated on a Cron based Schedule
     */
    public static <T> CompletableFuture<T> schedule(final String cron,
                                                    final ScheduledExecutorService ex,
                                                    final Supplier<T> t) {
        return Future.schedule(cron,
                               ex,
                               t)
                     .getFuture();
    }

    /**
     * Schedule the population of a CompletableFuture from the provided Supplier after the specified delay. The provided
     * ScheduledExecutorService provided the thread on which the Supplier will be executed.
     * <pre>
     * {@code
     *
     *    CompletableFuture<String> future = CompletableFutures.schedule(10l, Executors.newScheduledThreadPool(1), ()->"hello");
     *
     *    //CompletableFuture["hello"]
     *
     * }</pre>
     *
     * @param delay Delay after which the CompletableFuture should be populated
     * @param ex    ScheduledExecutorService used to execute the provided Supplier
     * @param t     he Supplier to execute to populate the CompletableFuture
     * @return CompletableFuture populated after the specified delay
     */
    public static <T> CompletableFuture<T> schedule(final long delay,
                                                    final ScheduledExecutorService ex,
                                                    final Supplier<T> t) {
        return Future.schedule(delay,
                               ex,
                               t)
                     .getFuture();
    }

    /**
     * Combine an CompletableFuture with the provided Iterable (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  CompletableFutures.zip(CompletableFuture.completedFuture(10),Arrays.asList(20), this::add)
     *  //CompletableFuture[30]
     *
     *  private int add(int a, int b) {
     * return a + b;
     * }
     *
     * }
     * </pre>
     *
     * @param f  CompletableFuture to combine with first element in Iterable (if present)
     * @param v  Iterable to combine
     * @param fn Combining function
     * @return CompletableFuture combined with supplied Iterable
     */
    public static <T1, T2, R> CompletableFuture<R> zip(final CompletableFuture<? extends T1> f,
                                                       final Iterable<? extends T2> v,
                                                       final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(Future.of(f)
                            .zip(v,
                                 fn)
                            .getFuture());
    }

    public static <T1, T2, R> CompletableFuture<R> zip(final CompletableFuture<? extends T1> f,
                                                       final CompletableFuture<? extends T2> v,
                                                       final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return zip(f,
                   Future.of(v),
                   fn);
    }

    /**
     * Combine an CompletableFuture with the provided Publisher (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  CompletableFutures.zip(Flux.just(10),CompletableFuture.completedResult(10), this::add)
     *  //CompletableFuture[30]
     *
     *  private int add(int a, int b) {
     * return a + b;
     * }
     *
     * }
     * </pre>
     *
     * @param p  Publisher to combine
     * @param f  CompletableFuture to combine with
     * @param fn Combining function
     * @return CompletableFuture combined with supplied Publisher
     */
    public static <T1, T2, R> CompletableFuture<R> zip(final Publisher<? extends T2> p,
                                                       final CompletableFuture<? extends T1> f,
                                                       final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(Future.of(f)
                            .zip(fn,
                                 p)
                            .getFuture());
    }

    /**
     * Narrow covariant type parameter
     *
     * @param f CompletableFuture with covariant type parameter
     * @return Narrowed Future
     */
    public static <T> CompletableFuture<T> narrow(final CompletableFuture<? extends T> f) {
        return (CompletableFuture<T>) f;
    }


}
