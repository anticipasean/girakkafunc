package cyclops.pure.instances.control;

import static cyclops.container.control.Ior.narrowK;

import cyclops.function.higherkinded.DataWitness.ior;
import cyclops.function.higherkinded.Higher;
import cyclops.function.higherkinded.Higher2;
import cyclops.pure.arrow.Cokleisli;
import cyclops.pure.arrow.Kleisli;
import cyclops.pure.arrow.MonoidK;
import cyclops.container.control.Either;
import cyclops.container.control.Ior;
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
import lombok.AllArgsConstructor;

public class IorInstances {

    private final static IorTypeclasses INSTANCE = new IorTypeclasses<>();

    public static <L, T> Kleisli<Higher<ior, L>, Ior<L, T>, T> kindKleisli() {
        return Kleisli.of(IorInstances.monad(),
                          Ior::widen);
    }

    public static <L, T> Cokleisli<Higher<ior, L>, T, Ior<L, T>> kindCokleisli() {
        return Cokleisli.of(Ior::narrowK);
    }

    public static <W1, ST, PT> Nested<Higher<ior, ST>, W1, PT> nested(Ior<ST, Higher<W1, PT>> nested,
                                                                      InstanceDefinitions<W1> def2) {
        return Nested.of(nested,
                         IorInstances.definitions(),
                         def2);
    }

    public static <W1, LT, RT> Product<Higher<ior, LT>, W1, RT> product(Ior<LT, RT> ior,
                                                                        Active<W1, RT> active) {
        return Product.of(allTypeclasses(ior),
                          active);
    }

    public static <W1, LT, RT> Coproduct<W1, Higher<ior, LT>, RT> coproduct(Ior<LT, RT> ior,
                                                                            InstanceDefinitions<W1> def2) {
        return Coproduct.right(ior,
                               def2,
                               IorInstances.definitions());
    }

    public static <LT, RT> Active<Higher<ior, LT>, RT> allTypeclasses(Ior<LT, RT> ior) {
        return Active.of(ior,
                         IorInstances.definitions());
    }

    public static <W2, R, LT, RT> Nested<Higher<ior, LT>, W2, R> mapM(Ior<LT, RT> ior,
                                                                      Function<? super RT, ? extends Higher<W2, R>> fn,
                                                                      InstanceDefinitions<W2> defs) {
        return Nested.of(ior.map(fn),
                         IorInstances.definitions(),
                         defs);
    }

    public static <L> InstanceDefinitions<Higher<ior, L>> definitions() {
        return new InstanceDefinitions<Higher<ior, L>>() {


            @Override
            public <T, R> Functor<Higher<ior, L>> functor() {
                return IorInstances.functor();
            }

            @Override
            public <T> Pure<Higher<ior, L>> unit() {
                return IorInstances.unit();
            }

            @Override
            public <T, R> Applicative<Higher<ior, L>> applicative() {
                return IorInstances.applicative();
            }

            @Override
            public <T, R> Monad<Higher<ior, L>> monad() {
                return IorInstances.monad();
            }

            @Override
            public <T, R> Option<MonadZero<Higher<ior, L>>> monadZero() {
                return Option.none();
            }

            @Override
            public <T> Option<MonadPlus<Higher<ior, L>>> monadPlus() {
                return Option.none();
            }

            @Override
            public <T> MonadRec<Higher<ior, L>> monadRec() {
                return IorInstances.monadRec();
            }

            @Override
            public <T> Option<MonadPlus<Higher<ior, L>>> monadPlus(MonoidK<Higher<ior, L>> m) {
                return Option.none();
            }

            @Override
            public <C2, T> Traverse<Higher<ior, L>> traverse() {
                return IorInstances.traverse();
            }

            @Override
            public <T> Foldable<Higher<ior, L>> foldable() {
                return IorInstances.foldable();
            }

            @Override
            public <T> Option<Comonad<Higher<ior, L>>> comonad() {
                return Option.none();
            }

            @Override
            public <T> Option<Unfoldable<Higher<ior, L>>> unfoldable() {
                return Option.none();
            }
        };
    }

    public static final <L> IorTypeclasses<L> getInstance() {
        return INSTANCE;
    }

    public static <L> Functor<Higher<ior, L>> functor() {
        return INSTANCE;
    }

    public static <L> Pure<Higher<ior, L>> unit() {
        return INSTANCE;
    }

    public static <L> Applicative<Higher<ior, L>> applicative() {
        return INSTANCE;
    }

    public static BiFunctor<ior> bifunctor() {
        return INSTANCE;
    }

    public static <L> Monad<Higher<ior, L>> monad() {
        return INSTANCE;
    }

    public static <L> Traverse<Higher<ior, L>> traverse() {
        return INSTANCE;
    }

    public static <L> Foldable<Higher<ior, L>> foldable() {
        return INSTANCE;
    }

    public static <X, T, R> MonadRec<Higher<ior, X>> monadRec() {
        return INSTANCE;
    }

    @AllArgsConstructor
    public static class IorTypeclasses<L> implements Monad<Higher<ior, L>>, MonadRec<Higher<ior, L>>,
                                                     TraverseByTraverse<Higher<ior, L>>, Foldable<Higher<ior, L>>,
                                                     ApplicativeError<Higher<ior, L>, L>, BiFunctor<ior> {

        @Override
        public <T> T foldRight(Monoid<T> monoid,
                               Higher<Higher<ior, L>, T> ds) {
            Ior<L, T> ior = narrowK(ds);
            return ior.fold(monoid);
        }

        @Override
        public <T> T foldLeft(Monoid<T> monoid,
                              Higher<Higher<ior, L>, T> ds) {
            Ior<L, T> ior = narrowK(ds);
            return ior.fold(monoid);
        }

        @Override
        public <T, R> R foldMap(Monoid<R> mb,
                                Function<? super T, ? extends R> fn,
                                Higher<Higher<ior, L>, T> nestedA) {
            return narrowK(nestedA).<R>map(fn).fold(mb);
        }

        @Override
        public <T, R, T2, R2> Higher2<ior, R, R2> bimap(Function<? super T, ? extends R> fn,
                                                        Function<? super T2, ? extends R2> fn2,
                                                        Higher2<ior, T, T2> ds) {
            return narrowK(ds).bimap(fn,
                                     fn2);
        }

        @Override
        public <T> Higher<Higher<ior, L>, T> raiseError(L l) {
            return Ior.left(l);
        }

        @Override
        public <T> Higher<Higher<ior, L>, T> handleErrorWith(Function<? super L, ? extends Higher<Higher<ior, L>, ? extends T>> fn,
                                                             Higher<Higher<ior, L>, T> ds) {
            Function<? super L, ? extends Ior<L, T>> fn2 = fn.andThen(s -> {

                Higher<Higher<ior, L>, T> x = (Higher<Higher<ior, L>, T>) s;
                Ior<L, T> r = Ior.narrowK(x);
                return r;
            });
            Ior<L, T> ior = Ior.narrowK(ds);
            if (ior.isLeft()) {
                Ior<L, ? extends T> res = narrowK(fn.apply(ior.getLeft()
                                                              .orElse(null)));
                return (Ior<L, T>) res;
            }
            return ior;
        }

        @Override
        public <T, R> Higher<Higher<ior, L>, R> flatMap(Function<? super T, ? extends Higher<Higher<ior, L>, R>> fn,
                                                        Higher<Higher<ior, L>, T> ds) {
            Ior<L, T> ior = narrowK(ds);
            return ior.flatMap(fn.andThen(Ior::narrowK));
        }

        @Override
        public <C2, T, R> Higher<C2, Higher<Higher<ior, L>, R>> traverseA(Applicative<C2> applicative,
                                                                          Function<? super T, ? extends Higher<C2, R>> fn,
                                                                          Higher<Higher<ior, L>, T> ds) {
            Ior<L, T> maybe = narrowK(ds);

            return maybe.fold(left -> applicative.unit(Ior.left(left)),
                              right -> applicative.map(m -> Ior.right(m),
                                                       fn.apply(right)),
                              (l, r) -> applicative.map(m -> Ior.both(l,
                                                                      m),
                                                        fn.apply(r)));
        }

        @Override
        public <T, R> Higher<Higher<ior, L>, R> ap(Higher<Higher<ior, L>, ? extends Function<T, R>> fn,
                                                   Higher<Higher<ior, L>, T> apply) {
            Ior<L, T> ior = narrowK(apply);
            Ior<L, ? extends Function<T, R>> iorFn = narrowK(fn);
            return iorFn.zip(ior,
                             (a, b) -> a.apply(b));
        }

        @Override
        public <T> Higher<Higher<ior, L>, T> unit(T value) {
            return Ior.right(value);
        }

        @Override
        public <T, R> Higher<Higher<ior, L>, R> map(Function<? super T, ? extends R> fn,
                                                    Higher<Higher<ior, L>, T> ds) {
            Ior<L, T> ior = narrowK(ds);
            return ior.map(fn);
        }

        @Override
        public <T, R> Higher<Higher<ior, L>, R> tailRec(T initial,
                                                        Function<? super T, ? extends Higher<Higher<ior, L>, ? extends Either<T, R>>> fn) {
            Ior<L, ? extends Either<T, R>>[] next = new Ior[1];
            next[0] = Ior.right(Either.left(initial));
            boolean cont = true;
            do {
                cont = next[0].fold(p -> p.fold(s -> {
                                                    next[0] = narrowK(fn.apply(s));
                                                    return true;
                                                },
                                                pr -> false),
                                    () -> false);
            } while (cont);
            return next[0].map(x -> x.orElse(null));
        }


    }


}
