package cyclops.pure.instances.control;

import static cyclops.container.control.LazyEither.narrowK;

import cyclops.function.higherkinded.DataWitness.either;
import cyclops.function.higherkinded.Higher;
import cyclops.function.higherkinded.Higher2;
import cyclops.pure.arrow.Cokleisli;
import cyclops.pure.arrow.Kleisli;
import cyclops.pure.arrow.MonoidK;
import cyclops.container.control.Either;
import cyclops.container.control.LazyEither;
import cyclops.container.control.Option;
import cyclops.function.combiner.Monoid;
import cyclops.pure.container.functional.Active;
import cyclops.pure.container.functional.Coproduct;
import cyclops.pure.container.functional.Nested;
import cyclops.pure.container.functional.Product;
import cyclops.pure.typeclasses.InstanceDefinitions;
import cyclops.pure.typeclasses.Pure;
import cyclops.pure.typeclasses.comonad.Comonad;
import cyclops.pure.typeclasses.foldable.Foldable;
import cyclops.pure.typeclasses.foldable.Unfoldable;
import cyclops.pure.typeclasses.functor.BiFunctor;
import cyclops.pure.typeclasses.functor.Functor;
import cyclops.pure.typeclasses.monad.Applicative;
import cyclops.pure.typeclasses.monad.ApplicativeError;
import cyclops.pure.typeclasses.monad.Monad;
import cyclops.pure.typeclasses.monad.MonadPlus;
import cyclops.pure.typeclasses.monad.MonadRec;
import cyclops.pure.typeclasses.monad.MonadZero;
import cyclops.pure.typeclasses.monad.Traverse;
import cyclops.pure.typeclasses.monad.TraverseByTraverse;
import java.util.function.Function;

public class LazyEitherInstances {


    private final static LazyEitherTypeclasses INSTANCE = new LazyEitherTypeclasses<>();

    public static <W1, LT, RT> Product<Higher<either, LT>, W1, RT> product(Either<LT, RT> e,
                                                                           Active<W1, RT> active) {
        return Product.of(allTypeclasses(e),
                          active);
    }

    public static <W1, LT, RT> Coproduct<W1, Higher<either, LT>, RT> coproduct(Either<LT, RT> e,
                                                                               InstanceDefinitions<W1> def2) {
        return Coproduct.right(e,
                               def2,
                               LazyEitherInstances.definitions());
    }

    public static <LT, RT> Active<Higher<either, LT>, RT> allTypeclasses(Either<LT, RT> e) {
        return Active.of(e,
                         LazyEitherInstances.definitions());
    }

    public static <W2, R, LT, RT> Nested<Higher<either, LT>, W2, R> mapM(Either<LT, RT> e,
                                                                         Function<? super RT, ? extends Higher<W2, R>> fn,
                                                                         InstanceDefinitions<W2> defs) {
        return Nested.of(e.map(fn),
                         LazyEitherInstances.definitions(),
                         defs);
    }

    public static <L, T> Kleisli<Higher<either, L>, Either<L, T>, T> kindKleisli() {
        return Kleisli.of(LazyEitherInstances.monad(),
                          Either::widen);
    }

    public static <L, T> Cokleisli<Higher<either, L>, T, Either<L, T>> kindCokleisli() {
        return Cokleisli.of(Either::narrowK);
    }

    public static <W1, ST, PT> Nested<Higher<either, ST>, W1, PT> nested(Either<ST, Higher<W1, PT>> nested,
                                                                         InstanceDefinitions<W1> def2) {
        return Nested.of(nested,
                         LazyEitherInstances.definitions(),
                         def2);
    }

    public static <L> InstanceDefinitions<Higher<either, L>> definitions() {
        return new InstanceDefinitions<Higher<either, L>>() {
            @Override
            public <T, R> Functor<Higher<either, L>> functor() {
                return LazyEitherInstances.functor();
            }

            @Override
            public <T> Pure<Higher<either, L>> unit() {
                return LazyEitherInstances.unit();
            }

            @Override
            public <T, R> Applicative<Higher<either, L>> applicative() {
                return LazyEitherInstances.applicative();
            }

            @Override
            public <T, R> Monad<Higher<either, L>> monad() {
                return LazyEitherInstances.monad();
            }

            @Override
            public <T, R> Option<MonadZero<Higher<either, L>>> monadZero() {
                return Option.none();
            }

            @Override
            public <T> Option<MonadPlus<Higher<either, L>>> monadPlus() {
                return Option.none();
            }

            @Override
            public <T> MonadRec<Higher<either, L>> monadRec() {
                return LazyEitherInstances.monadRec();
            }


            @Override
            public <T> Option<MonadPlus<Higher<either, L>>> monadPlus(MonoidK<Higher<either, L>> m) {
                return Option.none();
            }

            @Override
            public <C2, T> Traverse<Higher<either, L>> traverse() {
                return LazyEitherInstances.traverse();
            }

            @Override
            public <T> Foldable<Higher<either, L>> foldable() {
                return LazyEitherInstances.foldable();
            }

            @Override
            public <T> Option<Comonad<Higher<either, L>>> comonad() {
                return Option.none();
            }

            @Override
            public <T> Option<Unfoldable<Higher<either, L>>> unfoldable() {
                return Option.none();
            }
        };
    }

    public static final <L> LazyEitherTypeclasses<L> getInstance() {
        return INSTANCE;
    }

    public static <L> Functor<Higher<either, L>> functor() {
        return INSTANCE;
    }

    public static <L> Pure<Higher<either, L>> unit() {
        return INSTANCE;
    }

    public static <L> Applicative<Higher<either, L>> applicative() {
        return INSTANCE;
    }

    public static <L> Monad<Higher<either, L>> monad() {
        return INSTANCE;
    }

    public static <X, T, R> MonadRec<Higher<either, X>> monadRec() {

        return INSTANCE;

    }

    public static BiFunctor<either> bifunctor() {
        return INSTANCE;
    }

    public static <L> Traverse<Higher<either, L>> traverse() {
        return INSTANCE;
    }

    public static <L> Foldable<Higher<either, L>> foldable() {
        return INSTANCE;
    }

    public static <L> ApplicativeError<Higher<either, L>, L> applicativeError() {
        return INSTANCE;
    }


    public static class LazyEitherTypeclasses<L> implements Monad<Higher<either, L>>, MonadRec<Higher<either, L>>,
                                                            TraverseByTraverse<Higher<either, L>>, Foldable<Higher<either, L>>,
                                                            ApplicativeError<Higher<either, L>, L>, BiFunctor<either> {

        public LazyEitherTypeclasses() {
        }

        @Override
        public <T> T foldRight(Monoid<T> monoid,
                               Higher<Higher<either, L>, T> ds) {
            Either<L, T> xor = narrowK(ds);
            return xor.fold(monoid);
        }

        @Override
        public <T> T foldLeft(Monoid<T> monoid,
                              Higher<Higher<either, L>, T> ds) {
            Either<L, T> xor = narrowK(ds);
            return xor.fold(monoid);
        }

        @Override
        public <T, R, T2, R2> Higher2<either, R, R2> bimap(Function<? super T, ? extends R> fn,
                                                           Function<? super T2, ? extends R2> fn2,
                                                           Higher2<either, T, T2> ds) {
            return narrowK(ds).bimap(fn,
                                     fn2);
        }

        @Override
        public <T, R> R foldMap(Monoid<R> mb,
                                Function<? super T, ? extends R> fn,
                                Higher<Higher<either, L>, T> nestedA) {
            return foldLeft(mb,
                            narrowK(nestedA).map(fn));
        }

        @Override
        public <T> Higher<Higher<either, L>, T> raiseError(L l) {
            return LazyEither.left(l);
        }

        @Override
        public <T> Higher<Higher<either, L>, T> handleErrorWith(Function<? super L, ? extends Higher<Higher<either, L>, ? extends T>> fn,
                                                                Higher<Higher<either, L>, T> ds) {
            Function<? super L, ? extends Either<L, T>> fn2 = fn.andThen(s -> {

                Higher<Higher<either, L>, T> x = (Higher<Higher<either, L>, T>) s;
                Either<L, T> r = narrowK(x);
                return r;
            });
            return narrowK(ds).flatMapLeftToRight(fn2);
        }

        @Override
        public <T, R> Higher<Higher<either, L>, R> flatMap(Function<? super T, ? extends Higher<Higher<either, L>, R>> fn,
                                                           Higher<Higher<either, L>, T> ds) {
            Either<L, T> xor = narrowK(ds);
            return xor.flatMap(fn.andThen(Either::narrowK));
        }

        @Override
        public <C2, T, R> Higher<C2, Higher<Higher<either, L>, R>> traverseA(Applicative<C2> applicative,
                                                                             Function<? super T, ? extends Higher<C2, R>> fn,
                                                                             Higher<Higher<either, L>, T> ds) {
            Either<L, T> maybe = narrowK(ds);
            return maybe.fold(left -> applicative.unit(Either.left(left)),
                              right -> applicative.map(m -> LazyEither.right(m),
                                                       fn.apply(right)));
        }

        @Override
        public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn,
                                                      Higher<Higher<either, L>, T> apply) {
            Either<L, T> xor = narrowK(apply);
            Either<L, ? extends Function<T, R>> xorFn = narrowK(fn);
            return xorFn.zip(xor,
                             (a, b) -> a.apply(b));
        }

        @Override
        public <T> Higher<Higher<either, L>, T> unit(T value) {
            return LazyEither.right(value);
        }

        @Override
        public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn,
                                                       Higher<Higher<either, L>, T> ds) {
            Either<L, T> xor = narrowK(ds);
            return xor.map(fn);
        }

        @Override
        public <T, R> Higher<Higher<either, L>, R> tailRec(T initial,
                                                           Function<? super T, ? extends Higher<Higher<either, L>, ? extends Either<T, R>>> fn) {
            return LazyEither.tailRec(initial,
                                      fn.andThen(LazyEither::narrowK));
        }
    }

}
