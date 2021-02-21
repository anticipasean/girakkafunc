package cyclops.reactive.collection.container.immutable;

import static cyclops.function.evaluation.Evaluation.LAZY;

import cyclops.async.Future;
import cyclops.container.control.Either;
import cyclops.container.control.option.Option;
import cyclops.container.immutable.impl.HashSet;
import cyclops.container.immutable.impl.Seq;
import cyclops.container.immutable.impl.Vector;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.container.immutable.tuple.Tuple3;
import cyclops.container.immutable.tuple.Tuple4;
import cyclops.container.persistent.PersistentCollection;
import cyclops.container.persistent.PersistentSet;
import cyclops.container.recoverable.OnEmptySwitch;
import cyclops.container.transformable.To;
import cyclops.exception.ExceptionSoftener;
import cyclops.function.combiner.Monoid;
import cyclops.function.combiner.Reducer;
import cyclops.function.companion.Reducers;
import cyclops.function.enhanced.Function3;
import cyclops.function.enhanced.Function4;
import cyclops.function.evaluation.Evaluation;
import cyclops.function.higherkinded.Higher;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.collection.container.CollectionX;
import cyclops.reactive.collection.container.ReactiveConvertableSequence;
import cyclops.reactive.collection.container.immutable.lazy.impl.LazyPSetX;
import cyclops.reactive.collection.container.lazy.LazyCollectionX;
import cyclops.reactive.collection.container.mutable.ListX;
import cyclops.reactive.collection.function.higherkinded.ReactiveWitness.persistentSetX;
import cyclops.reactive.collection.function.reducer.ReactiveReducers;
import cyclops.reactive.companion.Spouts;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.reactivestreams.Publisher;

/**
 * An eXtended Persistent Set type, that offers additional functional style operators such as bimap, filter and more Can operate
 * eagerly, lazily or reactively (async push)
 *
 * @param <T> the type of elements held in this collection
 * @author johnmcclean
 */
public interface PersistentSetX<T> extends To<PersistentSetX<T>>, PersistentSet<T>, Higher<persistentSetX, T>, LazyCollectionX<T>,
                                           OnEmptySwitch<T, PersistentSet<T>> {

    static <T> PersistentSetX<T> defer(Supplier<PersistentSetX<T>> s) {
        return of(s).map(Supplier::get)
                    .concatMap(l -> l);
    }

    static <T> CompletablePersistentSetX<T> completable() {
        return new CompletablePersistentSetX<>();
    }

    static <T> Higher<persistentSetX, T> widen(PersistentSetX<T> narrow) {
        return narrow;
    }

    /**
     * Narrow a covariant PersistentSetX
     *
     * <pre>
     * {@code
     *  PersistentSetX<? extends Fruit> set = PersistentSetX.of(apple,bannana);
     *  PersistentSetX<Fruit> fruitSet = PersistentSetX.narrowK(set);
     * }
     * </pre>
     *
     * @param setX to narrowK generic type
     * @return PersistentSetX with narrowed type
     */
    static <T> PersistentSetX<T> narrow(final PersistentSetX<? extends T> setX) {
        return (PersistentSetX<T>) setX;
    }

    /**
     * Create a PersistentSetX that contains the Integers between skip and take
     *
     * @param start Number of range to skip from
     * @param end   Number for range to take at
     * @return Range PersistentSetX
     */
    static PersistentSetX<Integer> range(final int start,
                                         final int end) {
        return ReactiveSeq.range(start,
                                 end)
                          .to(ReactiveConvertableSequence::converter)
                          .persistentSetX(LAZY);
    }

    /**
     * Create a PersistentSetX that contains the Longs between skip and take
     *
     * @param start Number of range to skip from
     * @param end   Number for range to take at
     * @return Range PersistentSetX
     */
    static PersistentSetX<Long> rangeLong(final long start,
                                          final long end) {
        return ReactiveSeq.rangeLong(start,
                                     end)
                          .to(ReactiveConvertableSequence::converter)
                          .persistentSetX(LAZY);
    }

    /**
     * Unfold a function into a PersistentSetX
     *
     * <pre>
     * {@code
     *  PersistentSetX.unfold(1,i->i<=6 ? Optional.of(Tuple.tuple(i,i+1)) : Optional.zero());
     *
     * //(1,2,3,4,5) in any order
     *
     * }</code>
     *
     * @param seed Initial value
     * @param unfolder Iteratively applied function, terminated by an zero Optional
     * @return PersistentSetX generated by unfolder function
     */
    static <U, T> PersistentSetX<T> unfold(final U seed,
                                           final Function<? super U, Option<Tuple2<T, U>>> unfolder) {
        return ReactiveSeq.unfold(seed,
                                  unfolder)
                          .to(ReactiveConvertableSequence::converter)
                          .persistentSetX(LAZY);
    }

    /**
     * Generate a PersistentSetX from the provided Supplier up to the provided limit number of times
     *
     * @param limit Max number of elements to generate
     * @param s     Supplier to generate PersistentSetX elements
     * @return PersistentSetX generated from the provided Supplier
     */
    static <T> PersistentSetX<T> generate(final long limit,
                                          final Supplier<T> s) {

        return ReactiveSeq.generate(s)
                          .limit(limit)
                          .to(ReactiveConvertableSequence::converter)
                          .persistentSetX(LAZY);
    }

    /**
     * Create a PersistentSetX by iterative application of a function to an initial element up to the supplied limit number of
     * times
     *
     * @param limit Max number of elements to generate
     * @param seed  Initial element
     * @param f     Iteratively applied to each element to generate the next element
     * @return PersistentSetX generated by iterative application
     */
    static <T> PersistentSetX<T> iterate(final long limit,
                                         final T seed,
                                         final UnaryOperator<T> f) {
        return ReactiveSeq.iterate(seed,
                                   f)
                          .limit(limit)
                          .to(ReactiveConvertableSequence::converter)
                          .persistentSetX(LAZY);

    }

    static <T> PersistentSetX<T> of(final T... values) {

        return new LazyPSetX<>(null,
                               ReactiveSeq.of(values),
                               Reducers.toPersistentSet(),
                               LAZY);
    }

    static <T> PersistentSetX<T> empty() {
        return new LazyPSetX<>(HashSet.empty(),
                               null,
                               Reducers.toPersistentSet(),
                               LAZY);
    }

    static <T> PersistentSetX<T> singleton(final T value) {
        return new LazyPSetX<>(HashSet.of(value),
                               null,
                               Reducers.toPersistentSet(),
                               LAZY);
    }

    /**
     * <pre>
     * {@code
     *  import static cyclops.stream.ReactiveSeq.range;
     *
     *  PeristentSetX<Integer> bag = persistentSetX(range(10,20));
     *
     * }
     * </pre>
     *
     * @param stream To create a PersistentSetX from
     * @param <T>    PersistentSetX generated from Stream
     * @return
     */
    static <T> PersistentSetX<T> persistentSetX(ReactiveSeq<T> stream) {
        return new LazyPSetX<>(null,
                               stream,
                               Reducers.toPersistentSet(),
                               LAZY);
    }

    static <T> PersistentSetX<T> fromIterable(final Iterable<T> iterable) {
        if (iterable instanceof PersistentSetX) {
            return (PersistentSetX) iterable;
        }
        if (iterable instanceof PersistentSet) {
            return new LazyPSetX<>((PersistentSet) iterable,
                                   null,
                                   Reducers.toPersistentSet(),
                                   LAZY);
        }

        return new LazyPSetX<>(null,
                               ReactiveSeq.fromIterable(iterable),
                               Reducers.toPersistentSet(),
                               LAZY);
    }

    /**
     * Construct a PersistentSetX from an Publisher
     *
     * @param publisher to construct PersistentSetX from
     * @return PersistentSetX
     */
    static <T> PersistentSetX<T> fromPublisher(final Publisher<T> publisher) {
        return Spouts.from(publisher)
                     .to(ReactiveConvertableSequence::converter)
                     .persistentSetX(LAZY);
    }

    static <T> PersistentSetX<T> fromIterator(Iterator<T> iterator) {
        return fromIterable(() -> iterator);
    }

    /**
     * default ConvertableSequence<T> to(){
     * <p>
     * return new ConvertableSequence<>(this); } default Collectable<T> collectors(){
     * <p>
     * <p>
     * return Seq.seq(this); }
     **/

    static <T> PersistentSetX<T> narrowK(final Higher<persistentSetX, T> persistentSetX) {
        return (PersistentSetX<T>) persistentSetX;
    }

    static <T, R> PersistentSetX<R> tailRec(T initial,
                                            Function<? super T, ? extends PersistentSetX<? extends Either<T, R>>> fn) {
        ListX<Either<T, R>> lazy = ListX.of(Either.left(initial));
        ListX<Either<T, R>> next = lazy.eager();
        boolean[] newValue = {true};
        for (; ; ) {

            next = next.concatMap(e -> e.fold(s -> {
                                                  newValue[0] = true;
                                                  return fn.apply(s);
                                              },
                                              p -> {
                                                  newValue[0] = false;
                                                  return ListX.of(e);
                                              }));
            if (!newValue[0]) {
                break;
            }

        }
        return Either.sequenceRight(next)
                     .orElse(ReactiveSeq.empty())
                     .to(ReactiveConvertableSequence::converter)
                     .persistentSetX(Evaluation.LAZY);
    }

    PersistentSetX<T> lazy();

    PersistentSetX<T> eager();

    PersistentSetX<T> type(Reducer<? extends PersistentSet<T>, T> reducer);

    default <T> PersistentSetX<T> fromStream(final ReactiveSeq<T> stream) {
        return ReactiveReducers.<T>toPersistentSetX().foldMap(stream);
    }

    /* (non-Javadoc)
     * @see CollectionX#forEach4(java.util.function.Function, java.util.function.BiFunction, com.oath.cyclops.util.function.TriFunction, com.oath.cyclops.util.function.QuadFunction)
     */
    @Override
    default <R1, R2, R3, R> PersistentSetX<R> forEach4(Function<? super T, ? extends Iterable<R1>> stream1,
                                                       BiFunction<? super T, ? super R1, ? extends Iterable<R2>> stream2,
                                                       Function3<? super T, ? super R1, ? super R2, ? extends Iterable<R3>> stream3,
                                                       Function4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return (PersistentSetX) LazyCollectionX.super.forEach4(stream1,
                                                               stream2,
                                                               stream3,
                                                               yieldingFunction);
    }

    /* (non-Javadoc)
     * @see CollectionX#forEach4(java.util.function.Function, java.util.function.BiFunction, com.oath.cyclops.util.function.TriFunction, com.oath.cyclops.util.function.QuadFunction, com.oath.cyclops.util.function.QuadFunction)
     */
    @Override
    default <R1, R2, R3, R> PersistentSetX<R> forEach4(Function<? super T, ? extends Iterable<R1>> stream1,
                                                       BiFunction<? super T, ? super R1, ? extends Iterable<R2>> stream2,
                                                       Function3<? super T, ? super R1, ? super R2, ? extends Iterable<R3>> stream3,
                                                       Function4<? super T, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                       Function4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return (PersistentSetX) LazyCollectionX.super.forEach4(stream1,
                                                               stream2,
                                                               stream3,
                                                               filterFunction,
                                                               yieldingFunction);
    }

    /* (non-Javadoc)
     * @see CollectionX#forEach3(java.util.function.Function, java.util.function.BiFunction, com.oath.cyclops.util.function.TriFunction)
     */
    @Override
    default <R1, R2, R> PersistentSetX<R> forEach3(Function<? super T, ? extends Iterable<R1>> stream1,
                                                   BiFunction<? super T, ? super R1, ? extends Iterable<R2>> stream2,
                                                   Function3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return (PersistentSetX) LazyCollectionX.super.forEach3(stream1,
                                                               stream2,
                                                               yieldingFunction);
    }

    /* (non-Javadoc)
     * @see CollectionX#forEach3(java.util.function.Function, java.util.function.BiFunction, com.oath.cyclops.util.function.TriFunction, com.oath.cyclops.util.function.TriFunction)
     */
    @Override
    default <R1, R2, R> PersistentSetX<R> forEach3(Function<? super T, ? extends Iterable<R1>> stream1,
                                                   BiFunction<? super T, ? super R1, ? extends Iterable<R2>> stream2,
                                                   Function3<? super T, ? super R1, ? super R2, Boolean> filterFunction,
                                                   Function3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return (PersistentSetX) LazyCollectionX.super.forEach3(stream1,
                                                               stream2,
                                                               filterFunction,
                                                               yieldingFunction);
    }

    /* (non-Javadoc)
     * @see CollectionX#forEach2(java.util.function.Function, java.util.function.BiFunction)
     */
    @Override
    default <R1, R> PersistentSetX<R> forEach2(Function<? super T, ? extends Iterable<R1>> stream1,
                                               BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return (PersistentSetX) LazyCollectionX.super.forEach2(stream1,
                                                               yieldingFunction);
    }

    /* (non-Javadoc)
     * @see CollectionX#forEach2(java.util.function.Function, java.util.function.BiFunction, java.util.function.BiFunction)
     */
    @Override
    default <R1, R> PersistentSetX<R> forEach2(Function<? super T, ? extends Iterable<R1>> stream1,
                                               BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                               BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return (PersistentSetX) LazyCollectionX.super.forEach2(stream1,
                                                               filterFunction,
                                                               yieldingFunction);
    }

    /**
     * coflatMap pattern, can be used to perform lazy reductions / collections / folds and other terminal operations
     *
     * <pre>
     * {@code
     *
     *     PersistentSetX.of(1,2,3)
     *          .map(i->i*2)
     *          .coflatMap(s -> s.reduce(0,(a,b)->a+b))
     *
     *     //PersistentSetX[12]
     * }
     * </pre>
     *
     * @param fn mapping function
     * @return Transformed PersistentSetX
     */
    default <R> PersistentSetX<R> coflatMap(Function<? super PersistentSetX<T>, ? extends R> fn) {
        return fn.andThen(r -> this.<R>unit(r))
                 .apply(this);
    }

    /**
     * Combine two adjacent elements in a PersistentSetX using the supplied BinaryOperator This is a stateful grouping & reduction
     * operation. The emitted of a combination may in turn be combined with it's neighbor
     * <pre>
     * {@code
     *  PersistentSetX.of(1,1,2,3)
     * .combine((a, b)->a.equals(b),SemigroupK.intSum)
     * .listX()
     *
     *  //ListX(3,4)
     * }</pre>
     *
     * @param predicate Test to see if two neighbors should be joined
     * @param op        Reducer to combine neighbors
     * @return Combined / Partially Reduced PersistentSetX
     */
    @Override
    default PersistentSetX<T> combine(final BiPredicate<? super T, ? super T> predicate,
                                      final BinaryOperator<T> op) {
        return (PersistentSetX<T>) LazyCollectionX.super.combine(predicate,
                                                                 op);
    }

    @Override
    default PersistentSetX<T> combine(final Monoid<T> op,
                                      final BiPredicate<? super T, ? super T> predicate) {
        return (PersistentSetX<T>) LazyCollectionX.super.combine(op,
                                                                 predicate);
    }

    @Override
    default boolean isEmpty() {
        return PersistentSet.super.isEmpty();
    }

    @Override
    default <R> PersistentSetX<R> unit(final Iterable<R> col) {
        return fromIterable(col);
    }

    @Override
    default <R> PersistentSetX<R> unit(final R value) {
        return singleton(value);
    }

    @Override
    default <R> PersistentSetX<R> unitIterable(final Iterable<R> it) {
        return fromIterable(it);
    }

    // @Override
    default <R> PersistentSetX<R> emptyUnit() {
        return empty();
    }

    @Override
    default PersistentSetX<T> materialize() {
        return (PersistentSetX<T>) LazyCollectionX.super.materialize();
    }

    @Override
    default ReactiveSeq<T> stream() {

        return ReactiveSeq.fromIterable(this);
    }

    @Override
    default boolean containsValue(T item) {
        return LazyCollectionX.super.containsValue(item);
    }

    @Override
    default <X> PersistentSetX<X> from(final Iterable<X> col) {
        return fromIterable(col);
    }

    //   @Override
    default <T> Reducer<PersistentSet<T>, T> monoid() {
        return Reducers.toPersistentSet();
    }

    /* (non-Javadoc)
     * @see org.pcollections.PSet#plus(java.lang.Object)
     */
    @Override
    PersistentSetX<T> plus(T e);

    /* (non-Javadoc)
     * @see org.pcollections.PSet#insertAt(java.util.Collection)
     */
    @Override
    PersistentSetX<T> plusAll(Iterable<? extends T> list);

    /* (non-Javadoc)
     * @see org.pcollections.PSet#removeValue(java.lang.Object)
     */
    @Override
    PersistentSetX<T> removeValue(T e);

    /* (non-Javadoc)
     * @see org.pcollections.PSet#removeAll(java.util.Collection)
     */
    @Override
    PersistentSetX<T> removeAll(Iterable<? extends T> list);

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#reverse()
     */
    @Override
    default PersistentSetX<T> reverse() {
        return (PersistentSetX<T>) LazyCollectionX.super.reverse();
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#filter(java.util.function.Predicate)
     */
    @Override
    default PersistentSetX<T> filter(final Predicate<? super T> pred) {
        return (PersistentSetX<T>) LazyCollectionX.super.filter(pred);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#transform(java.util.function.Function)
     */
    @Override
    default <R> PersistentSetX<R> map(final Function<? super T, ? extends R> mapper) {
        return (PersistentSetX<R>) LazyCollectionX.super.map(mapper);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#flatMap(java.util.function.Function)
     */
    @Override
    default <R> PersistentSetX<R> concatMap(final Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return (PersistentSetX<R>) LazyCollectionX.super.concatMap(mapper);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#slice(long, long)
     */
    @Override
    default PersistentSetX<T> slice(final long from,
                                    final long to) {
        return (PersistentSetX<T>) LazyCollectionX.super.slice(from,
                                                               to);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#sorted(java.util.function.Function)
     */
    @Override
    default <U extends Comparable<? super U>> PersistentSetX<T> sorted(final Function<? super T, ? extends U> function) {
        return (PersistentSetX<T>) LazyCollectionX.super.sorted(function);
    }

    @Override
    default PersistentSetX<Vector<T>> grouped(final int groupSize) {
        return (PersistentSetX<Vector<T>>) LazyCollectionX.super.grouped(groupSize);
    }

    @Override
    default <U> PersistentSetX<Tuple2<T, U>> zip(final Iterable<? extends U> other) {
        return (PersistentSetX) LazyCollectionX.super.zip(other);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#zip(java.lang.Iterable, java.util.function.BiFunction)
     */
    @Override
    default <U, R> PersistentSetX<R> zip(final Iterable<? extends U> other,
                                         final BiFunction<? super T, ? super U, ? extends R> zipper) {

        return (PersistentSetX<R>) LazyCollectionX.super.zip(other,
                                                             zipper);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#permutations()
     */
    @Override
    default PersistentSetX<ReactiveSeq<T>> permutations() {

        return (PersistentSetX<ReactiveSeq<T>>) LazyCollectionX.super.permutations();
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#combinations(int)
     */
    @Override
    default PersistentSetX<ReactiveSeq<T>> combinations(final int size) {

        return (PersistentSetX<ReactiveSeq<T>>) LazyCollectionX.super.combinations(size);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#combinations()
     */
    @Override
    default PersistentSetX<ReactiveSeq<T>> combinations() {

        return (PersistentSetX<ReactiveSeq<T>>) LazyCollectionX.super.combinations();
    }

    @Override
    default PersistentSetX<Seq<T>> sliding(final int windowSize) {
        return (PersistentSetX<Seq<T>>) LazyCollectionX.super.sliding(windowSize);
    }

    @Override
    default PersistentSetX<Seq<T>> sliding(final int windowSize,
                                           final int increment) {
        return (PersistentSetX<Seq<T>>) LazyCollectionX.super.sliding(windowSize,
                                                                      increment);
    }

    @Override
    default PersistentSetX<T> scanLeft(final Monoid<T> monoid) {
        return (PersistentSetX<T>) LazyCollectionX.super.scanLeft(monoid);
    }

    @Override
    default <U> PersistentSetX<U> scanLeft(final U seed,
                                           final BiFunction<? super U, ? super T, ? extends U> function) {
        return (PersistentSetX<U>) LazyCollectionX.super.scanLeft(seed,
                                                                  function);
    }

    @Override
    default PersistentSetX<T> scanRight(final Monoid<T> monoid) {
        return (PersistentSetX<T>) LazyCollectionX.super.scanRight(monoid);
    }

    @Override
    default <U> PersistentSetX<U> scanRight(final U identity,
                                            final BiFunction<? super T, ? super U, ? extends U> combiner) {
        return (PersistentSetX<U>) LazyCollectionX.super.scanRight(identity,
                                                                   combiner);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#plusInOrder(java.lang.Object)
     */
    @Override
    default PersistentSetX<T> plusInOrder(final T e) {

        return (PersistentSetX<T>) LazyCollectionX.super.plusInOrder(e);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.standard.LazyCollectionX#cycle(int)
     */
    @Override
    default LinkedListX<T> cycle(final long times) {

        return this.stream()
                   .cycle(times)
                   .to(ReactiveConvertableSequence::converter)
                   .linkedListX(LAZY);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.standard.LazyCollectionX#cycle(com.oath.cyclops.sequence.Monoid, int)
     */
    @Override
    default LinkedListX<T> cycle(final Monoid<T> m,
                                 final long times) {

        return this.stream()
                   .cycle(m,
                          times)
                   .to(ReactiveConvertableSequence::converter)
                   .linkedListX(LAZY);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.standard.LazyCollectionX#cycleWhile(java.util.function.Predicate)
     */
    @Override
    default LinkedListX<T> cycleWhile(final Predicate<? super T> predicate) {

        return this.stream()
                   .cycleWhile(predicate)
                   .to(ReactiveConvertableSequence::converter)
                   .linkedListX(LAZY);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.standard.LazyCollectionX#cycleUntil(java.util.function.Predicate)
     */
    @Override
    default LinkedListX<T> cycleUntil(final Predicate<? super T> predicate) {

        return this.stream()
                   .cycleUntil(predicate)
                   .to(ReactiveConvertableSequence::converter)
                   .linkedListX(LAZY);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#zip(java.util.stream.Stream)
     */
    @Override
    default <U> PersistentSetX<Tuple2<T, U>> zipWithStream(final Stream<? extends U> other) {
        return (PersistentSetX) LazyCollectionX.super.zipWithStream(other);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#zip3(java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    default <S, U> PersistentSetX<Tuple3<T, S, U>> zip3(final Iterable<? extends S> second,
                                                        final Iterable<? extends U> third) {

        return (PersistentSetX) LazyCollectionX.super.zip3(second,
                                                           third);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#zip4(java.util.stream.Stream, java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    default <T2, T3, T4> PersistentSetX<Tuple4<T, T2, T3, T4>> zip4(final Iterable<? extends T2> second,
                                                                    final Iterable<? extends T3> third,
                                                                    final Iterable<? extends T4> fourth) {

        return (PersistentSetX) LazyCollectionX.super.zip4(second,
                                                           third,
                                                           fourth);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#zipWithIndex()
     */
    @Override
    default PersistentSetX<Tuple2<T, Long>> zipWithIndex() {

        return (PersistentSetX<Tuple2<T, Long>>) LazyCollectionX.super.zipWithIndex();
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#distinct()
     */
    @Override
    default PersistentSetX<T> distinct() {

        return (PersistentSetX<T>) LazyCollectionX.super.distinct();
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#sorted()
     */
    @Override
    default PersistentSetX<T> sorted() {

        return (PersistentSetX<T>) LazyCollectionX.super.sorted();
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#sorted(java.util.Comparator)
     */
    @Override
    default PersistentSetX<T> sorted(final Comparator<? super T> c) {

        return (PersistentSetX<T>) LazyCollectionX.super.sorted(c);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#skipWhile(java.util.function.Predicate)
     */
    default PersistentSetX<T> dropWhile(final Predicate<? super T> p) {

        return (PersistentSetX<T>) LazyCollectionX.super.dropWhile(p);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#skipUntil(java.util.function.Predicate)
     */
    default PersistentSetX<T> dropUntil(final Predicate<? super T> p) {

        return (PersistentSetX<T>) LazyCollectionX.super.dropUntil(p);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#limitWhile(java.util.function.Predicate)
     */
    default PersistentSetX<T> takeWhile(final Predicate<? super T> p) {

        return (PersistentSetX<T>) LazyCollectionX.super.takeWhile(p);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#limitUntil(java.util.function.Predicate)
     */
    default PersistentSetX<T> takeUntil(final Predicate<? super T> p) {

        return (PersistentSetX<T>) LazyCollectionX.super.takeUntil(p);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#intersperse(java.lang.Object)
     */
    @Override
    default PersistentSetX<T> intersperse(final T value) {

        return (PersistentSetX<T>) LazyCollectionX.super.intersperse(value);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#shuffle()
     */
    @Override
    default PersistentSetX<T> shuffle() {

        return (PersistentSetX<T>) LazyCollectionX.super.shuffle();
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#skipLast(int)
     */
    default PersistentSetX<T> dropRight(final int num) {

        return (PersistentSetX<T>) LazyCollectionX.super.dropRight(num);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#limitLast(int)
     */
    default PersistentSetX<T> takeRight(final int num) {

        return (PersistentSetX<T>) LazyCollectionX.super.takeRight(num);
    }

    /* (non-Javadoc)
     * @see cyclops.data.recoverable.OnEmptySwitch#onEmptySwitch(java.util.function.Supplier)
     */
    @Override
    default PersistentSetX<T> onEmptySwitch(final Supplier<? extends PersistentSet<T>> supplier) {
        if (this.isEmpty()) {
            return PersistentSetX.fromIterable(supplier.get());
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#onEmpty(java.lang.Object)
     */
    @Override
    default PersistentSetX<T> onEmpty(final T value) {

        return (PersistentSetX<T>) LazyCollectionX.super.onEmpty(value);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#onEmptyGet(java.util.function.Supplier)
     */
    @Override
    default PersistentSetX<T> onEmptyGet(final Supplier<? extends T> supplier) {

        return (PersistentSetX<T>) LazyCollectionX.super.onEmptyGet(supplier);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#onEmptyError(java.util.function.Supplier)
     */
    @Override
    default <X extends Throwable> PersistentSetX<T> onEmptyError(final Supplier<? extends X> supplier) {

        return (PersistentSetX<T>) LazyCollectionX.super.onEmptyError(supplier);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#shuffle(java.util.Random)
     */
    @Override
    default PersistentSetX<T> shuffle(final Random random) {

        return (PersistentSetX<T>) LazyCollectionX.super.shuffle(random);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#ofType(java.lang.Class)
     */
    @Override
    default <U> PersistentSetX<U> ofType(final Class<? extends U> type) {

        return (PersistentSetX<U>) LazyCollectionX.super.ofType(type);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#filterNot(java.util.function.Predicate)
     */
    @Override
    default PersistentSetX<T> filterNot(final Predicate<? super T> fn) {

        return (PersistentSetX<T>) LazyCollectionX.super.filterNot(fn);
    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.collections.extensions.persistent.LazyCollectionX#notNull()
     */
    @Override
    default PersistentSetX<T> notNull() {

        return (PersistentSetX<T>) LazyCollectionX.super.notNull();
    }

    @Override
    default PersistentSetX<T> removeStream(final Stream<? extends T> stream) {

        return (PersistentSetX<T>) LazyCollectionX.super.removeStream(stream);
    }

    @Override
    default PersistentSetX<T> removeAll(final T... values) {

        return (PersistentSetX<T>) LazyCollectionX.super.removeAll(values);
    }

    @Override
    default PersistentSetX<T> removeAll(CollectionX<? extends T> it) {
        return removeAll((Iterable<T>) it);
    }

    @Override
    default PersistentSetX<T> retainAll(final Iterable<? extends T> it) {

        return (PersistentSetX<T>) LazyCollectionX.super.retainAll(it);
    }

    @Override
    default PersistentSetX<T> retainStream(final Stream<? extends T> seq) {

        return (PersistentSetX<T>) LazyCollectionX.super.retainStream(seq);
    }

    @Override
    default PersistentSetX<T> retainAll(final T... values) {

        return (PersistentSetX<T>) LazyCollectionX.super.retainAll(values);
    }

    @Override
    default <C extends PersistentCollection<? super T>> PersistentSetX<C> grouped(final int size,
                                                                                  final Supplier<C> supplier) {

        return (PersistentSetX<C>) LazyCollectionX.super.grouped(size,
                                                                 supplier);
    }

    @Override
    default PersistentSetX<Vector<T>> groupedUntil(final Predicate<? super T> predicate) {

        return (PersistentSetX<Vector<T>>) LazyCollectionX.super.groupedUntil(predicate);
    }

    @Override
    default PersistentSetX<Vector<T>> groupedUntil(final BiPredicate<Vector<? super T>, ? super T> predicate) {

        return (PersistentSetX<Vector<T>>) LazyCollectionX.super.groupedUntil(predicate);
    }

    @Override
    default PersistentSetX<Vector<T>> groupedWhile(final Predicate<? super T> predicate) {

        return (PersistentSetX<Vector<T>>) LazyCollectionX.super.groupedWhile(predicate);
    }

    @Override
    default <C extends PersistentCollection<? super T>> PersistentSetX<C> groupedWhile(final Predicate<? super T> predicate,
                                                                                       final Supplier<C> factory) {

        return (PersistentSetX<C>) LazyCollectionX.super.groupedWhile(predicate,
                                                                      factory);
    }

    @Override
    default <C extends PersistentCollection<? super T>> PersistentSetX<C> groupedUntil(final Predicate<? super T> predicate,
                                                                                       final Supplier<C> factory) {

        return (PersistentSetX<C>) LazyCollectionX.super.groupedUntil(predicate,
                                                                      factory);
    }

    @Override
    default <R> PersistentSetX<R> retry(final Function<? super T, ? extends R> fn) {
        return (PersistentSetX<R>) LazyCollectionX.super.retry(fn);
    }

    @Override
    default <R> PersistentSetX<R> retry(final Function<? super T, ? extends R> fn,
                                        final int retries,
                                        final long delay,
                                        final TimeUnit timeUnit) {
        return (PersistentSetX<R>) LazyCollectionX.super.retry(fn,
                                                               retries,
                                                               delay,
                                                               timeUnit);
    }

    @Override
    default <R> PersistentSetX<R> flatMap(Function<? super T, ? extends Stream<? extends R>> fn) {
        return (PersistentSetX<R>) LazyCollectionX.super.flatMap(fn);
    }

    @Override
    default <R> PersistentSetX<R> mergeMap(Function<? super T, ? extends Publisher<? extends R>> fn) {
        return (PersistentSetX<R>) LazyCollectionX.super.mergeMap(fn);
    }

    @Override
    default <R> PersistentSetX<R> mergeMap(int maxConcurency,
                                           Function<? super T, ? extends Publisher<? extends R>> fn) {
        return (PersistentSetX<R>) LazyCollectionX.super.mergeMap(maxConcurency,
                                                                  fn);
    }

    @Override
    default PersistentSetX<T> removeFirst(Predicate<? super T> pred) {
        return (PersistentSetX<T>) LazyCollectionX.super.removeFirst(pred);
    }

    @Override
    default PersistentSetX<T> appendAll(Iterable<? extends T> value) {
        return (PersistentSetX<T>) LazyCollectionX.super.appendAll(value);
    }

    @Override
    default PersistentSetX<T> prependAll(Iterable<? extends T> value) {
        return (PersistentSetX<T>) LazyCollectionX.super.prependAll(value);
    }

    @Override
    default PersistentSetX<T> updateAt(int pos,
                                       T value) {
        return (PersistentSetX<T>) LazyCollectionX.super.updateAt(pos,
                                                                  value);
    }

    @Override
    default PersistentSetX<T> insertAt(int pos,
                                       ReactiveSeq<? extends T> values) {
        return (PersistentSetX<T>) LazyCollectionX.super.insertAt(pos,
                                                                  values);
    }

    @Override
    default PersistentSetX<T> prependStream(Stream<? extends T> stream) {
        return (PersistentSetX<T>) LazyCollectionX.super.prependStream(stream);
    }

    @Override
    default PersistentSetX<T> appendAll(T... values) {
        return (PersistentSetX<T>) LazyCollectionX.super.appendAll(values);
    }

    @Override
    default PersistentSetX<T> append(T value) {
        return (PersistentSetX<T>) LazyCollectionX.super.append(value);
    }

    @Override
    default PersistentSetX<T> prepend(T value) {
        return (PersistentSetX<T>) LazyCollectionX.super.prepend(value);
    }

    @Override
    default PersistentSetX<T> prependAll(T... values) {
        return (PersistentSetX<T>) LazyCollectionX.super.prependAll(values);
    }

    @Override
    default PersistentSetX<T> insertAt(int pos,
                                       T values) {
        LazyCollectionX<T> r = LazyCollectionX.super.insertAt(pos,
                                                              values);
        return (PersistentSetX<T>) r;
    }

    @Override
    default PersistentSetX<T> insertAt(int pos,
                                       T... values) {
        LazyCollectionX<T> r = LazyCollectionX.super.insertAt(pos,
                                                              values);
        return (PersistentSetX<T>) r;
    }

    @Override
    default PersistentSetX<T> deleteBetween(int start,
                                            int end) {
        return (PersistentSetX<T>) LazyCollectionX.super.deleteBetween(start,
                                                                       end);
    }

    @Override
    default PersistentSetX<T> insertStreamAt(int pos,
                                             Stream<T> stream) {
        return (PersistentSetX<T>) LazyCollectionX.super.insertStreamAt(pos,
                                                                        stream);
    }

    @Override
    default PersistentSetX<T> recover(final Function<? super Throwable, ? extends T> fn) {
        return (PersistentSetX<T>) LazyCollectionX.super.recover(fn);
    }

    @Override
    default <EX extends Throwable> PersistentSetX<T> recover(Class<EX> exceptionClass,
                                                             final Function<? super EX, ? extends T> fn) {
        return (PersistentSetX<T>) LazyCollectionX.super.recover(exceptionClass,
                                                                 fn);
    }

    @Override
    default PersistentSetX<T> plusLoop(int max,
                                       IntFunction<T> value) {
        return (PersistentSetX<T>) LazyCollectionX.super.plusLoop(max,
                                                                  value);
    }

    @Override
    default PersistentSetX<T> plusLoop(Supplier<Option<T>> supplier) {
        return (PersistentSetX<T>) LazyCollectionX.super.plusLoop(supplier);
    }

    @Override
    default <T2, R> PersistentSetX<R> zip(final BiFunction<? super T, ? super T2, ? extends R> fn,
                                          final Publisher<? extends T2> publisher) {
        return (PersistentSetX<R>) LazyCollectionX.super.zip(fn,
                                                             publisher);
    }

    @Override
    default <U> PersistentSetX<Tuple2<T, U>> zipWithPublisher(final Publisher<? extends U> other) {
        return (PersistentSetX) LazyCollectionX.super.zipWithPublisher(other);
    }

    @Override
    default <S, U, R> PersistentSetX<R> zip3(final Iterable<? extends S> second,
                                             final Iterable<? extends U> third,
                                             final Function3<? super T, ? super S, ? super U, ? extends R> fn3) {
        return (PersistentSetX<R>) LazyCollectionX.super.zip3(second,
                                                              third,
                                                              fn3);
    }

    @Override
    default <T2, T3, T4, R> PersistentSetX<R> zip4(final Iterable<? extends T2> second,
                                                   final Iterable<? extends T3> third,
                                                   final Iterable<? extends T4> fourth,
                                                   final Function4<? super T, ? super T2, ? super T3, ? super T4, ? extends R> fn) {
        return (PersistentSetX<R>) LazyCollectionX.super.zip4(second,
                                                              third,
                                                              fourth,
                                                              fn);
    }

    class CompletablePersistentSetX<T> implements InvocationHandler {

        Future<PersistentSetX<T>> future = Future.future();

        public boolean complete(PersistentSet<T> result) {
            return future.complete(PersistentSetX.fromIterable(result));
        }

        public PersistentSetX<T> asPersistentSetX() {
            PersistentSetX f = (PersistentSetX) Proxy.newProxyInstance(PersistentQueueX.class.getClassLoader(),
                                                                       new Class[]{PersistentSetX.class},
                                                                       this);
            return f;
        }

        @Override
        public Object invoke(Object proxy,
                             Method method,
                             Object[] args) throws Throwable {
            PersistentSetX<T> target = future.fold(l -> l,
                                                   t -> {
                                                       throw ExceptionSoftener.throwSoftenedException(t);
                                                   });
            return method.invoke(target,
                                 args);
        }
    }

}