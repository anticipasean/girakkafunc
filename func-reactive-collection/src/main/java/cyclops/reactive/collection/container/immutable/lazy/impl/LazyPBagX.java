package cyclops.reactive.collection.container.immutable.lazy.impl;


import cyclops.container.control.Option;
import cyclops.container.immutable.impl.Bag;
import cyclops.container.persistent.PersistentBag;
import cyclops.function.combiner.Reducer;
import cyclops.function.evaluation.Evaluation;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.collection.container.immutable.BagX;
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
public class LazyPBagX<T> extends AbstractLazyPersistentCollection<T, PersistentBag<T>> implements BagX<T> {

    public LazyPBagX(PersistentBag<T> list,
                     ReactiveSeq<T> seq,
                     Reducer<PersistentBag<T>, T> reducer,
                     Evaluation strict) {
        super(list,
              seq,
              reducer,
              strict,
              asyncBag());
    }

    public static final <T> Function<ReactiveSeq<PersistentBag<T>>, PersistentBag<T>> asyncBag() {
        return r -> {
            CompletableBagX<T> res = BagX.completable();
            r.forEachAsync(l -> res.complete(l));
            return res.asBagX();
        };
    }

    @Override
    public BagX<T> plusLoop(int max,
                            IntFunction<T> value) {
        return (BagX<T>) super.plusLoop(max,
                                        value);
    }

    @Override
    public BagX<T> plusLoop(Supplier<Option<T>> supplier) {
        return (BagX<T>) super.plusLoop(supplier);
    }


    //@Override
    public BagX<T> materialize() {
        get();
        return this;
    }

    public BagX<T> type(Reducer<? extends PersistentBag<T>, T> reducer) {
        Reducer<PersistentBag<T>, T> narrow = Reducer.narrow(reducer);
        return new LazyPBagX<T>(list,
                                seq.get(),
                                narrow,
                                evaluation());
    }


    @Override
    public BagX<T> lazy() {
        return new LazyPBagX<T>(list,
                                seq.get(),
                                getCollectorInternal(),
                                Evaluation.LAZY);
    }

    @Override
    public BagX<T> eager() {
        return new LazyPBagX<T>(list,
                                seq.get(),
                                getCollectorInternal(),
                                Evaluation.EAGER);
    }

    @Override
    public <X> LazyPBagX<X> fromStream(ReactiveSeq<X> stream) {

        return new LazyPBagX<X>((PersistentBag) getList(),
                                ReactiveSeq.fromStream(stream),
                                (Reducer) this.getCollectorInternal(),
                                evaluation());
    }

    @Override
    public <T1> LazyPBagX<T1> from(Iterable<T1> c) {
        if (c instanceof PersistentBag) {
            return new LazyPBagX<T1>((PersistentBag) c,
                                     null,
                                     (Reducer) this.getCollectorInternal(),
                                     evaluation());
        }
        return fromStream(ReactiveSeq.fromIterable(c));
    }

    public <T1> LazyPBagX<T1> from(Bag<T1> c) {

        return new LazyPBagX<T1>(c,
                                 null,
                                 (Reducer) this.getCollectorInternal(),
                                 evaluation());

    }


    @Override
    public BagX<T> plus(T e) {
        return from(get().plus(e));
    }

    @Override
    public BagX<T> plusAll(Iterable<? extends T> list) {
        return from(get().plusAll(list));
    }


    @Override
    public BagX<T> removeAll(Iterable<? extends T> list) {
        return from(get().removeAll(list));
    }


    @Override
    public BagX<T> removeValue(T remove) {
        return from(get().removeValue(remove));
    }


    @Override
    public <U> LazyPBagX<U> unitIterable(Iterable<U> it) {
        return fromStream(ReactiveSeq.fromIterable(it));
    }


    @Override
    public <R> LazyPBagX<R> unit(Iterable<R> col) {
        return from(col);
    }


}
