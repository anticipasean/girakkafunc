package cyclops.pure.free;


import cyclops.function.higherkinded.DataWitness.cofree;
import cyclops.function.higherkinded.DataWitness.eval;
import cyclops.function.higherkinded.Higher;
import cyclops.function.higherkinded.Higher2;
import cyclops.container.control.Eval;
import cyclops.function.higherkinded.NaturalTransformation;
import cyclops.pure.instances.control.EvalInstances;
import cyclops.pure.typeclasses.comonad.Comonad;
import cyclops.pure.typeclasses.functor.Functor;
import cyclops.pure.typeclasses.monad.Monad;
import cyclops.pure.typeclasses.monad.Traverse;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/*
 * Cofree refs & guides : https://github.com/typelevel/cats/blob/master/free/src/main/scala/cats/free/Cofree.scala
 *                        https://github.com/kategory/kategory/blob/master/kategory/src/main/kotlin/kategory/free/Cofree.kt
 *
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Cofree<W, T> implements Supplier<T>, Higher2<cofree, W, T> {

    private final Functor<W> functor;
    private final T head;
    private final Eval<Higher<W, Cofree<W, T>>> tail;

    public static <W, T> Cofree<W, T> of(Functor<W> functor,
                                         T head,
                                         Eval<Higher<W, Cofree<W, T>>> tail) {
        return new Cofree<W, T>(functor,
                                head,
                                tail);
    }

    public static <W, T> Cofree<W, T> unfold(Functor<W> functor,
                                             T b,
                                             Function<? super T, ? extends Higher<W, T>> fn) {
        return of(functor,
                  b,
                  Eval.later(() -> functor.map_(fn.apply(b),
                                                t -> unfold(functor,
                                                            t,
                                                            fn))));
    }

    public static <W, T> Cofree<W, T> narrowK2(final Higher2<cofree, W, T> cof) {
        return (Cofree<W, T>) cof;
    }

    public static <W, T> Cofree<W, T> narrowK(final Higher<Higher<cofree, W>, T> cof) {
        return (Cofree<W, T>) cof;
    }

    public Higher<W, Cofree<W, T>> tailForced() {
        return tail.get();
    }

    public <R> Cofree<W, R> map(Function<? super T, ? extends R> f) {
        return transform(f,
                         c -> c.map(f));
    }

    public <R> Cofree<W, R> coflatMap(Function<? super Cofree<W, T>, ? extends R> f) {
        return of(functor,
                  f.apply(this),
                  tail.map(h -> functor.map_(h,
                                             __ -> coflatMap(f))));
    }

    public Cofree<W, Cofree<W, T>> nest() {
        return of(functor,
                  this,
                  tail.map(h -> functor.map_(h,
                                             __ -> nest())));
    }

    public <R> Cofree<W, R> transform(Function<? super T, ? extends R> f,
                                      Function<Cofree<W, T>, Cofree<W, R>> g) {
        return of(functor,
                  f.apply(head),
                  tail.map(i -> functor.map_(i,
                                             g)));
    }

    public Cofree<W, T> mapBranchingRoot(NaturalTransformation<W, W> nat) {
        return of(functor,
                  head,
                  tail.map(h -> nat.apply(h)));
    }

    public <R> Cofree<R, T> mapBranchingS(Functor<R> functor,
                                          NaturalTransformation<W, R> nat) {
        return of(functor,
                  head,
                  tail.map(ce -> nat.apply(this.functor.map_(ce,
                                                             cofree -> cofree.mapBranchingS(functor,
                                                                                            nat)))));
    }

    public <R> Cofree<R, T> mapBranchingT(Functor<R> functor,
                                          NaturalTransformation<W, R> nat) {
        return of(functor,
                  head,
                  tail.map(ce -> functor.map_(nat.apply(ce),
                                              cofree -> cofree.mapBranchingT(functor,
                                                                             nat))));
    }

    public Cofree<W, T> forceTail() {
        return of(functor,
                  head,
                  Eval.now(tail.get()));
    }

    public Cofree<W, T> forceAll() {
        return of(functor,
                  head,
                  Eval.now(tail.map(h -> functor.map_(h,
                                                      c -> c.forceAll()))
                               .get()));
    }

    public T extract() {
        return head;
    }

    public T get() {
        return extract();
    }

    public <R> Eval<R> fold(Traverse<W> traverse,
                            BiFunction<T, Higher<W, R>, Eval<R>> fn) {
        Eval<Higher<W, R>> eval = traverse.traverseA(EvalInstances.applicative(),
                                                     it -> it.fold(traverse,
                                                                   fn),
                                                     tailForced())
                                          .convert(Eval::narrowK);
        return eval.flatMap(i -> fn.apply(extract(),
                                          i));
    }

    public <M, R> Higher<M, R> visitM(Traverse<W> traverse,
                                      Monad<M> monad,
                                      BiFunction<? super T, ? super Higher<W, R>, Higher<M, R>> fn,
                                      NaturalTransformation<eval, M> inclusion) {

        class inner {

            public Eval<Higher<M, R>> loop(Cofree<W, T> eval) {
                Higher<M, Higher<W, R>> looped = traverse.traverseA(monad,
                                                                    (Cofree<W, T> fr) -> monad.flatten(inclusion.apply(Eval.defer(() -> loop(fr)))),
                                                                    eval.tailForced());
                Higher<M, R> folded = monad.flatMap_(looped,
                                                     fb -> fn.apply(eval.head,
                                                                    fb));
                return Eval.now(folded);
            }
        }
        return monad.flatten(inclusion.apply(new inner().loop(this)));
    }

    public static class Instances {

        public <W> Comonad<Higher<cofree, W>> comonad() {
            return new Comonad<Higher<cofree, W>>() {
                @Override
                public <T> T extract(Higher<Higher<cofree, W>, T> ds) {
                    return narrowK(ds).extract();
                }

                @Override
                public <T> Higher<Higher<cofree, W>, Higher<Higher<cofree, W>, T>> nest(Higher<Higher<cofree, W>, T> ds) {

                    return (Higher) narrowK(ds).nest();
                }

                @Override
                public <T, R> Higher<Higher<cofree, W>, R> coflatMap(Function<? super Higher<Higher<cofree, W>, T>, R> mapper,
                                                                     Higher<Higher<cofree, W>, T> ds) {
                    return narrowK(ds).coflatMap(mapper);
                }


            };
        }
    }


}
