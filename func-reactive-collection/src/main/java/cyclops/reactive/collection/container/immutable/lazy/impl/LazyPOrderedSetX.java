package cyclops.reactive.collection.container.immutable.lazy.impl;


import cyclops.container.control.Option;
import cyclops.container.persistent.PersistentSortedSet;
import cyclops.function.combiner.Reducer;
import cyclops.function.evaluation.Evaluation;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.collection.container.immutable.OrderedSetX;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * An extended List type {@see java.util.List} Extended List operations execute lazily e.g.
 * <pre>
 * {@code
 *    StreamX<Integer> q = StreamX.of(1,2,3)
 *                                      .map(i->i*2);
 * }
 * </pre>
 * The map operation above is not executed immediately. It will only be executed when (if) the data inside the queue is accessed.
 * This allows lazy operations to be chained and executed more efficiently e.g.
 *
 * <pre>
 * {@code
 *    DequeX<Integer> q = DequeX.of(1,2,3)
 *                              .map(i->i*2);
 *                              .filter(i->i<5);
 * }
 * </pre>
 * <p>
 * The operation above is more efficient than the equivalent operation with a ListX.
 *
 * @param <T> the type of elements held in this toX
 * @author johnmcclean
 */
public class LazyPOrderedSetX<T> extends AbstractLazyPersistentCollection<T, PersistentSortedSet<T>> implements OrderedSetX<T> {

    public LazyPOrderedSetX(PersistentSortedSet<T> list,
                            ReactiveSeq<T> seq,
                            Reducer<PersistentSortedSet<T>, T> reducer,
                            Evaluation strict) {
        super(list,
              seq,
              reducer,
              strict,
              asyncOrderedSet());


    }

    public static final <T> Function<ReactiveSeq<PersistentSortedSet<T>>, PersistentSortedSet<T>> asyncOrderedSet() {
        return r -> {
            CompletableOrderedSetX<T> res = new CompletableOrderedSetX<>();
            r.forEachAsync(l -> res.complete(l));
            return res.asOrderedSetX();
        };
    }

    //@Override
    public OrderedSetX<T> materialize() {
        get();
        return this;
    }


    @Override
    public OrderedSetX<T> type(Reducer<? extends PersistentSortedSet<T>, T> reducer) {
        return new LazyPOrderedSetX<T>(list,
                                       seq.get(),
                                       Reducer.narrow(reducer),
                                       evaluation());
    }

    //  @Override
    public <X> LazyPOrderedSetX<X> fromStream(ReactiveSeq<X> stream) {

        return new LazyPOrderedSetX<X>((PersistentSortedSet) getList(),
                                       ReactiveSeq.fromStream(stream),
                                       (Reducer) this.getCollectorInternal(),
                                       evaluation());
    }

    @Override
    public <T1> LazyPOrderedSetX<T1> from(Iterable<T1> c) {
        if (c instanceof PersistentSortedSet) {
            return new LazyPOrderedSetX<T1>((PersistentSortedSet) c,
                                            null,
                                            (Reducer) this.getCollectorInternal(),
                                            evaluation());
        }
        return fromStream(ReactiveSeq.fromIterable(c));
    }

    public <T1> LazyPOrderedSetX<T1> from(PersistentSortedSet<T1> c) {
        return new LazyPOrderedSetX<T1>(c,
                                        null,
                                        (Reducer) this.getCollectorInternal(),
                                        evaluation());
    }

    @Override
    public OrderedSetX<T> lazy() {
        return new LazyPOrderedSetX<T>(list,
                                       seq.get(),
                                       getCollectorInternal(),
                                       Evaluation.LAZY);
    }

    @Override
    public OrderedSetX<T> eager() {
        return new LazyPOrderedSetX<T>(list,
                                       seq.get(),
                                       getCollectorInternal(),
                                       Evaluation.EAGER);
    }


    @Override
    public OrderedSetX<T> plus(T e) {
        return from(get().plus(e));
    }

    @Override
    public OrderedSetX<T> plusAll(Iterable<? extends T> list) {
        return from(get().plusAll(list));
    }


    @Override
    public OrderedSetX<T> removeAll(Iterable<? extends T> list) {
        return from(get().removeAll(list));
    }

    @Override
    public Option<T> get(int index) {
        return get().get(index);
    }

    @Override
    public Comparator<? super T> comparator() {
        return get().comparator();
    }

    /**
     * @Override public int indexOf(Object o) { return getValue().indexOf(o); }
     **/

    @Override
    public OrderedSetX<T> removeValue(T remove) {
        return from(get().removeValue(remove));
    }


    @Override
    public <U> LazyPOrderedSetX<U> unitIterable(Iterable<U> it) {
        return fromStream(ReactiveSeq.fromIterable(it));
    }


    @Override
    public <R> LazyPOrderedSetX<R> unit(Iterable<R> col) {
        return from(col);
    }

    @Override
    public OrderedSetX<T> plusLoop(int max,
                                   IntFunction<T> value) {
        return (OrderedSetX<T>) super.plusLoop(max,
                                               value);
    }

    @Override
    public OrderedSetX<T> plusLoop(Supplier<Option<T>> supplier) {
        return (OrderedSetX<T>) super.plusLoop(supplier);
    }

}
