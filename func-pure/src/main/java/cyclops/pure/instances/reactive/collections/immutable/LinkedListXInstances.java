package cyclops.pure.instances.reactive.collections.immutable;

import static cyclops.reactive.collection.function.higherkinded.ReactiveWitness.linkedListX;
import static cyclops.reactive.collection.container.immutable.LinkedListX.narrowK;

import cyclops.function.higherkinded.Higher;
import cyclops.pure.arrow.Cokleisli;
import cyclops.pure.arrow.Kleisli;
import cyclops.pure.arrow.MonoidK;
import cyclops.pure.arrow.MonoidKs;
import cyclops.container.control.Either;
import cyclops.container.control.Option;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.function.combiner.Monoid;
import cyclops.pure.container.functional.Active;
import cyclops.pure.container.functional.Coproduct;
import cyclops.pure.container.functional.Nested;
import cyclops.pure.container.functional.Product;
import cyclops.reactive.collection.container.immutable.LinkedListX;
import cyclops.pure.typeclasses.InstanceDefinitions;
import cyclops.pure.typeclasses.Pure;
import cyclops.pure.typeclasses.comonad.Comonad;
import cyclops.pure.typeclasses.foldable.Foldable;
import cyclops.pure.typeclasses.foldable.Unfoldable;
import cyclops.pure.typeclasses.functor.Functor;
import cyclops.pure.typeclasses.monad.Applicative;
import cyclops.pure.typeclasses.monad.Monad;
import cyclops.pure.typeclasses.monad.MonadPlus;
import cyclops.pure.typeclasses.monad.MonadRec;
import cyclops.pure.typeclasses.monad.MonadZero;
import cyclops.pure.typeclasses.monad.Traverse;
import cyclops.pure.typeclasses.monad.TraverseByTraverse;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with reactive LinkedListX's
 *
 * @author johnmcclean
 */
@UtilityClass
public class LinkedListXInstances {

    private final static LinkedListXTypeClasses INSTANCE = new LinkedListXTypeClasses();

    public static <T> Kleisli<linkedListX, LinkedListX<T>, T> kindKleisli() {
        return Kleisli.of(LinkedListXInstances.monad(),
                          LinkedListX::widen);
    }

    public static <T> Cokleisli<linkedListX, T, LinkedListX<T>> kindCokleisli() {
        return Cokleisli.of(LinkedListX::narrowK);
    }

    public static <W1, T> Nested<linkedListX, W1, T> nested(LinkedListX<Higher<W1, T>> nested,
                                                            InstanceDefinitions<W1> def2) {
        return Nested.of(nested,
                         LinkedListXInstances.definitions(),
                         def2);
    }

    public static <W1, T> Product<linkedListX, W1, T> product(LinkedListX<T> l,
                                                              Active<W1, T> active) {
        return Product.of(allTypeclasses(l),
                          active);
    }

    public static <W1, T> Coproduct<W1, linkedListX, T> coproduct(LinkedListX<T> l,
                                                                  InstanceDefinitions<W1> def2) {
        return Coproduct.right(l,
                               def2,
                               LinkedListXInstances.definitions());
    }

    public static <T> Active<linkedListX, T> allTypeclasses(LinkedListX<T> l) {
        return Active.of(l,
                         LinkedListXInstances.definitions());
    }

    public static <W2, R, T> Nested<linkedListX, W2, R> mapM(LinkedListX<T> l,
                                                             Function<? super T, ? extends Higher<W2, R>> fn,
                                                             InstanceDefinitions<W2> defs) {
        return Nested.of(l.map(fn),
                         LinkedListXInstances.definitions(),
                         defs);
    }

    public static InstanceDefinitions<linkedListX> definitions() {
        return new InstanceDefinitions<linkedListX>() {
            @Override
            public <T, R> Functor<linkedListX> functor() {
                return LinkedListXInstances.functor();
            }

            @Override
            public <T> Pure<linkedListX> unit() {
                return LinkedListXInstances.unit();
            }

            @Override
            public <T, R> Applicative<linkedListX> applicative() {
                return LinkedListXInstances.zippingApplicative();
            }

            @Override
            public <T, R> Monad<linkedListX> monad() {
                return LinkedListXInstances.monad();
            }

            @Override
            public <T, R> Option<MonadZero<linkedListX>> monadZero() {
                return Option.some(LinkedListXInstances.monadZero());
            }

            @Override
            public <T> Option<MonadPlus<linkedListX>> monadPlus() {
                return Option.some(LinkedListXInstances.monadPlus());
            }

            @Override
            public <T> MonadRec<linkedListX> monadRec() {
                return LinkedListXInstances.monadRec();
            }

            @Override
            public <T> Option<MonadPlus<linkedListX>> monadPlus(MonoidK<linkedListX> m) {
                return Option.some(LinkedListXInstances.monadPlus(m));
            }

            @Override
            public <C2, T> Traverse<linkedListX> traverse() {
                return LinkedListXInstances.traverse();
            }

            @Override
            public <T> Foldable<linkedListX> foldable() {
                return LinkedListXInstances.foldable();
            }

            @Override
            public <T> Option<Comonad<linkedListX>> comonad() {
                return Option.none();
            }

            @Override
            public <T> Option<Unfoldable<linkedListX>> unfoldable() {
                return Option.some(LinkedListXInstances.unfoldable());
            }
        };
    }

    public static Pure<linkedListX> unit() {
        return INSTANCE;
    }

    public static Unfoldable<linkedListX> unfoldable() {

        return INSTANCE;
    }

    public static MonadPlus<linkedListX> monadPlus(MonoidK<linkedListX> m) {

        return INSTANCE.withMonoidK(m);
    }

    public static <T, R> Applicative<linkedListX> zippingApplicative() {
        return INSTANCE;
    }

    public static <T, R> Functor<linkedListX> functor() {
        return INSTANCE;
    }

    public static <T, R> Monad<linkedListX> monad() {
        return INSTANCE;
    }

    public static <T, R> MonadZero<linkedListX> monadZero() {

        return INSTANCE;
    }

    public static <T> MonadPlus<linkedListX> monadPlus() {

        return INSTANCE;
    }

    public static <T, R> MonadRec<linkedListX> monadRec() {

        return INSTANCE;
    }

    public static <C2, T> Traverse<linkedListX> traverse() {
        return INSTANCE;
    }

    public static <T, R> Foldable<linkedListX> foldable() {
        return INSTANCE;
    }

    @AllArgsConstructor
    @lombok.With
    public static class LinkedListXTypeClasses implements MonadPlus<linkedListX>, MonadRec<linkedListX>,
                                                          TraverseByTraverse<linkedListX>, Foldable<linkedListX>,
                                                          Unfoldable<linkedListX> {

        private final MonoidK<linkedListX> monoidK;

        public LinkedListXTypeClasses() {
            monoidK = MonoidKs.linkedListXConcat();
        }

        @Override
        public <T> Higher<linkedListX, T> filter(Predicate<? super T> predicate,
                                                 Higher<linkedListX, T> ds) {
            return narrowK(ds).filter(predicate);
        }

        @Override
        public <T, R> Higher<linkedListX, Tuple2<T, R>> zip(Higher<linkedListX, T> fa,
                                                            Higher<linkedListX, R> fb) {
            return narrowK(fa).zip(narrowK(fb));
        }

        @Override
        public <T1, T2, R> Higher<linkedListX, R> zip(Higher<linkedListX, T1> fa,
                                                      Higher<linkedListX, T2> fb,
                                                      BiFunction<? super T1, ? super T2, ? extends R> f) {
            return narrowK(fa).zip(narrowK(fb),
                                   f);
        }

        @Override
        public <T> MonoidK<linkedListX> monoid() {
            return monoidK;
        }

        @Override
        public <T, R> Higher<linkedListX, R> flatMap(Function<? super T, ? extends Higher<linkedListX, R>> fn,
                                                     Higher<linkedListX, T> ds) {
            return narrowK(ds).concatMap(i -> narrowK(fn.apply(i)));
        }

        @Override
        public <T, R> Higher<linkedListX, R> ap(Higher<linkedListX, ? extends Function<T, R>> fn,
                                                Higher<linkedListX, T> apply) {
            return narrowK(apply).zip(narrowK(fn),
                                      (a, b) -> b.apply(a));
        }

        @Override
        public <T> Higher<linkedListX, T> unit(T value) {
            return LinkedListX.of(value);
        }

        @Override
        public <T, R> Higher<linkedListX, R> map(Function<? super T, ? extends R> fn,
                                                 Higher<linkedListX, T> ds) {
            return narrowK(ds).map(fn);
        }


        @Override
        public <T, R> Higher<linkedListX, R> tailRec(T initial,
                                                     Function<? super T, ? extends Higher<linkedListX, ? extends Either<T, R>>> fn) {
            return LinkedListX.tailRec(initial,
                                       i -> narrowK(fn.apply(i)));
        }

        @Override
        public <C2, T, R> Higher<C2, Higher<linkedListX, R>> traverseA(Applicative<C2> ap,
                                                                       Function<? super T, ? extends Higher<C2, R>> fn,
                                                                       Higher<linkedListX, T> ds) {
            LinkedListX<T> v = narrowK(ds);
            return v.foldRight(ap.unit(LinkedListX.empty()),
                               (b, a) -> ap.zip(fn.apply(b),
                                                                                    a,
                                                                                    (sn, vec) -> narrowK(vec).plus(sn)));


        }

        @Override
        public <T, R> R foldMap(Monoid<R> mb,
                                Function<? super T, ? extends R> fn,
                                Higher<linkedListX, T> ds) {
            LinkedListX<T> x = narrowK(ds);
            return x.foldLeft(mb.zero(),
                              (a, b) -> mb.apply(a,
                                                 fn.apply(b)));
        }

        @Override
        public <T, R> Higher<linkedListX, Tuple2<T, Long>> zipWithIndex(Higher<linkedListX, T> ds) {
            return narrowK(ds).zipWithIndex();
        }

        @Override
        public <T> T foldRight(Monoid<T> monoid,
                               Higher<linkedListX, T> ds) {
            return narrowK(ds).foldRight(monoid);
        }


        @Override
        public <T> T foldLeft(Monoid<T> monoid,
                              Higher<linkedListX, T> ds) {
            return narrowK(ds).foldLeft(monoid);
        }


        @Override
        public <R, T> Higher<linkedListX, R> unfold(T b,
                                                    Function<? super T, Option<Tuple2<R, T>>> fn) {
            return LinkedListX.unfold(b,
                                      fn);
        }


    }


}
