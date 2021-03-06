package cyclops.pure.instances.reactive.collections.mutable;


import static cyclops.reactive.collection.function.higherkinded.ReactiveWitness.deque;
import static cyclops.reactive.collection.container.mutable.DequeX.narrowK;

import cyclops.function.higherkinded.Higher;
import cyclops.pure.arrow.Cokleisli;
import cyclops.pure.arrow.Kleisli;
import cyclops.pure.arrow.MonoidK;
import cyclops.pure.arrow.MonoidKs;
import cyclops.container.control.Either;
import cyclops.container.control.Maybe;
import cyclops.container.control.Option;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.function.combiner.Monoid;
import cyclops.pure.container.functional.Active;
import cyclops.pure.container.functional.Coproduct;
import cyclops.pure.container.functional.Nested;
import cyclops.pure.container.functional.Product;
import cyclops.reactive.collection.container.mutable.DequeX;
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

@UtilityClass
public class DequeXInstances {

    private final static DequeXTypeClasses INSTANCE = new DequeXTypeClasses();

    public static <T> Kleisli<deque, DequeX<T>, T> kindKleisli() {
        return Kleisli.of(DequeXInstances.monad(),
                          DequeX::widen);
    }

    public static <T> Cokleisli<deque, T, DequeX<T>> kindCokleisli() {
        return Cokleisli.of(DequeX::narrowK);
    }

    public static <W1, T> Nested<deque, W1, T> nested(DequeX<Higher<W1, T>> nested,
                                                      InstanceDefinitions<W1> def2) {
        return Nested.of(nested,
                         DequeXInstances.definitions(),
                         def2);
    }

    public static <W1, T> Product<deque, W1, T> product(DequeX<T> d,
                                                        Active<W1, T> active) {
        return Product.of(allTypeclasses(d),
                          active);
    }

    public static <W1, T> Coproduct<W1, deque, T> coproduct(DequeX<T> d,
                                                            InstanceDefinitions<W1> def2) {
        return Coproduct.right(d,
                               def2,
                               DequeXInstances.definitions());
    }

    public static <T> Active<deque, T> allTypeclasses(DequeX<T> d) {
        return Active.of(d,
                         DequeXInstances.definitions());
    }

    public static <W2, R, T> Nested<deque, W2, R> mapM(DequeX<T> d,
                                                       Function<? super T, ? extends Higher<W2, R>> fn,
                                                       InstanceDefinitions<W2> defs) {
        return Nested.of(d.map(fn),
                         DequeXInstances.definitions(),
                         defs);
    }

    public static InstanceDefinitions<deque> definitions() {
        return new InstanceDefinitions<deque>() {
            @Override
            public <T, R> Functor<deque> functor() {
                return DequeXInstances.functor();
            }

            @Override
            public <T> Pure<deque> unit() {
                return DequeXInstances.unit();
            }

            @Override
            public <T, R> Applicative<deque> applicative() {
                return DequeXInstances.zippingApplicative();
            }

            @Override
            public <T, R> Monad<deque> monad() {
                return DequeXInstances.monad();
            }

            @Override
            public <T, R> Option<MonadZero<deque>> monadZero() {
                return Option.some(DequeXInstances.monadZero());
            }

            @Override
            public <T> Option<MonadPlus<deque>> monadPlus() {
                return Option.some(DequeXInstances.monadPlus());
            }

            @Override
            public <T> MonadRec<deque> monadRec() {
                return DequeXInstances.monadRec();
            }

            @Override
            public <T> Option<MonadPlus<deque>> monadPlus(MonoidK<deque> m) {
                return Option.some(DequeXInstances.monadPlus(m));
            }

            @Override
            public <C2, T> Traverse<deque> traverse() {
                return DequeXInstances.traverse();
            }

            @Override
            public <T> Foldable<deque> foldable() {
                return DequeXInstances.foldable();
            }

            @Override
            public <T> Option<Comonad<deque>> comonad() {
                return Maybe.nothing();
            }

            @Override
            public <T> Option<Unfoldable<deque>> unfoldable() {
                return Option.some(DequeXInstances.unfoldable());
            }
        };
    }

    public static Pure<deque> unit() {
        return INSTANCE;
    }

    public static Unfoldable<deque> unfoldable() {

        return INSTANCE;
    }

    public static MonadPlus<deque> monadPlus(MonoidK<deque> m) {

        return INSTANCE.withMonoidK(m);
    }

    public static <T, R> Applicative<deque> zippingApplicative() {
        return INSTANCE;
    }

    public static <T, R> Functor<deque> functor() {
        return INSTANCE;
    }

    public static <T, R> Monad<deque> monad() {
        return INSTANCE;
    }

    public static <T, R> MonadZero<deque> monadZero() {

        return INSTANCE;
    }

    public static <T> MonadPlus<deque> monadPlus() {

        return INSTANCE;
    }

    public static <T, R> MonadRec<deque> monadRec() {

        return INSTANCE;
    }

    public static <C2, T> Traverse<deque> traverse() {
        return INSTANCE;
    }

    public static <T, R> Foldable<deque> foldable() {
        return INSTANCE;
    }

    @AllArgsConstructor
    @lombok.With
    public static class DequeXTypeClasses implements MonadPlus<deque>, MonadRec<deque>, TraverseByTraverse<deque>,
                                                     Foldable<deque>, Unfoldable<deque> {

        private final MonoidK<deque> monoidK;

        public DequeXTypeClasses() {
            monoidK = MonoidKs.dequeXConcat();
        }

        @Override
        public <T> Higher<deque, T> filter(Predicate<? super T> predicate,
                                           Higher<deque, T> ds) {
            return narrowK(ds).filter(predicate);
        }

        @Override
        public <T, R> Higher<deque, Tuple2<T, R>> zip(Higher<deque, T> fa,
                                                      Higher<deque, R> fb) {
            return narrowK(fa).zip(narrowK(fb));
        }

        @Override
        public <T1, T2, R> Higher<deque, R> zip(Higher<deque, T1> fa,
                                                Higher<deque, T2> fb,
                                                BiFunction<? super T1, ? super T2, ? extends R> f) {
            return narrowK(fa).zip(narrowK(fb),
                                   f);
        }

        @Override
        public <T> MonoidK<deque> monoid() {
            return monoidK;
        }

        @Override
        public <T, R> Higher<deque, R> flatMap(Function<? super T, ? extends Higher<deque, R>> fn,
                                               Higher<deque, T> ds) {
            return narrowK(ds).concatMap(i -> narrowK(fn.apply(i)));
        }

        @Override
        public <T, R> Higher<deque, R> ap(Higher<deque, ? extends Function<T, R>> fn,
                                          Higher<deque, T> apply) {
            return narrowK(apply).zip(narrowK(fn),
                                      (a, b) -> b.apply(a));
        }

        @Override
        public <T> Higher<deque, T> unit(T value) {
            return DequeX.of(value);
        }

        @Override
        public <T, R> Higher<deque, R> map(Function<? super T, ? extends R> fn,
                                           Higher<deque, T> ds) {
            return narrowK(ds).map(fn);
        }


        @Override
        public <T, R> Higher<deque, R> tailRec(T initial,
                                               Function<? super T, ? extends Higher<deque, ? extends Either<T, R>>> fn) {
            return DequeX.tailRec(initial,
                                  i -> narrowK(fn.apply(i)));
        }

        @Override
        public <C2, T, R> Higher<C2, Higher<deque, R>> traverseA(Applicative<C2> ap,
                                                                 Function<? super T, ? extends Higher<C2, R>> fn,
                                                                 Higher<deque, T> ds) {
            DequeX<T> v = narrowK(ds);
            return v.foldLeft(ap.unit(DequeX.empty()),
                              (a, b) -> ap.zip(fn.apply(b),
                                                                             a,
                                                                             (sn, vec) -> narrowK(vec).plus(sn)));


        }

        @Override
        public <T, R> R foldMap(Monoid<R> mb,
                                Function<? super T, ? extends R> fn,
                                Higher<deque, T> ds) {
            DequeX<T> x = narrowK(ds);
            return x.foldLeft(mb.zero(),
                              (a, b) -> mb.apply(a,
                                                 fn.apply(b)));
        }

        @Override
        public <T, R> Higher<deque, Tuple2<T, Long>> zipWithIndex(Higher<deque, T> ds) {
            return narrowK(ds).zipWithIndex();
        }

        @Override
        public <T> T foldRight(Monoid<T> monoid,
                               Higher<deque, T> ds) {
            return narrowK(ds).foldRight(monoid);
        }


        @Override
        public <T> T foldLeft(Monoid<T> monoid,
                              Higher<deque, T> ds) {
            return narrowK(ds).foldLeft(monoid);
        }


        @Override
        public <R, T> Higher<deque, R> unfold(T b,
                                              Function<? super T, Option<Tuple2<R, T>>> fn) {
            return DequeX.unfold(b,
                                 fn);
        }


    }


}
