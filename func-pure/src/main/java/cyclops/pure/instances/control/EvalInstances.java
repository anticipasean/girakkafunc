package cyclops.pure.instances.control;

import static cyclops.container.control.Eval.narrowK;

import cyclops.function.higherkinded.DataWitness.eval;
import cyclops.function.higherkinded.Higher;
import cyclops.pure.arrow.Cokleisli;
import cyclops.pure.arrow.Kleisli;
import cyclops.pure.arrow.MonoidK;
import cyclops.container.control.Either;
import cyclops.container.control.Eval;
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
import cyclops.pure.typeclasses.functor.Functor;
import cyclops.pure.typeclasses.monad.Applicative;
import cyclops.pure.typeclasses.monad.Monad;
import cyclops.pure.typeclasses.monad.MonadPlus;
import cyclops.pure.typeclasses.monad.MonadRec;
import cyclops.pure.typeclasses.monad.MonadZero;
import cyclops.pure.typeclasses.monad.Traverse;
import cyclops.pure.typeclasses.monad.TraverseByTraverse;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Evals
 *
 * @author johnmcclean
 */
@UtilityClass
public class EvalInstances {

    private final EvalTypeclasses INSTANCE = new EvalTypeclasses();

    public static <T> Kleisli<eval, Eval<T>, T> kindKleisli() {
        return Kleisli.of(EvalInstances.monad(),
                          Eval::widen);
    }

    public static <T> Cokleisli<eval, T, Eval<T>> kindCokleisli() {
        return Cokleisli.of(Eval::narrowK);
    }

    public static <W1, T> Nested<eval, W1, T> nested(Eval<Higher<W1, T>> nested,
                                                     InstanceDefinitions<W1> def2) {
        return Nested.of(nested,
                         EvalInstances.definitions(),
                         def2);
    }

    public static <W1, T> Product<eval, W1, T> product(Eval<T> ev,
                                                       Active<W1, T> active) {
        return Product.of(allTypeclasses(ev),
                          active);
    }

    public static <W1, T> Coproduct<W1, eval, T> coproduct(Eval<T> ev,
                                                           InstanceDefinitions<W1> def2) {
        return Coproduct.right(ev,
                               def2,
                               EvalInstances.definitions());
    }

    public static <T> Active<eval, T> allTypeclasses(Eval<T> ev) {
        return Active.of(ev,
                         EvalInstances.definitions());
    }

    public static <W2, R, T> Nested<eval, W2, R> mapM(Eval<T> ev,
                                                      Function<? super T, ? extends Higher<W2, R>> fn,
                                                      InstanceDefinitions<W2> defs) {
        return Nested.of(ev.map(fn),
                         EvalInstances.definitions(),
                         defs);
    }

    public static InstanceDefinitions<eval> definitions() {
        return new InstanceDefinitions<eval>() {
            @Override
            public <T, R> Functor<eval> functor() {
                return EvalInstances.functor();
            }

            @Override
            public <T> Pure<eval> unit() {
                return EvalInstances.unit();
            }

            @Override
            public <T, R> Applicative<eval> applicative() {
                return EvalInstances.applicative();
            }

            @Override
            public <T, R> Monad<eval> monad() {
                return EvalInstances.monad();
            }

            @Override
            public <T, R> Option<MonadZero<eval>> monadZero() {
                return Option.none();
            }

            @Override
            public <T> Option<MonadPlus<eval>> monadPlus() {
                return Option.none();
            }

            @Override
            public <T> MonadRec<eval> monadRec() {
                return EvalInstances.monadRec();
            }

            @Override
            public <T> Option<MonadPlus<eval>> monadPlus(MonoidK<eval> m) {
                return Option.none();
            }

            @Override
            public <C2, T> Traverse<eval> traverse() {
                return EvalInstances.traverse();
            }

            @Override
            public <T> Foldable<eval> foldable() {
                return EvalInstances.foldable();
            }

            @Override
            public <T> Option<Comonad<eval>> comonad() {
                return Option.some(EvalInstances.comonad());
            }

            @Override
            public <T> Option<Unfoldable<eval>> unfoldable() {
                return Option.none();
            }
        };
    }

    public static <T, R> Functor<eval> functor() {
        return INSTANCE;
    }

    public static <T> Pure<eval> unit() {
        return INSTANCE;
    }

    public static <T, R> Applicative<eval> applicative() {
        return INSTANCE;
    }

    public static <T, R> Monad<eval> monad() {
        return INSTANCE;
    }

    public static <T, R> Comonad<eval> comonad() {
        return INSTANCE;
    }

    public static <T, R> MonadRec<eval> monadRec() {

        return INSTANCE;
    }

    public static <C2, T> Traverse<eval> traverse() {
        return INSTANCE;
    }

    public static <T, R> Foldable<eval> foldable() {
        return INSTANCE;
    }

    @AllArgsConstructor
    public static class EvalTypeclasses implements Monad<eval>, MonadRec<eval>, TraverseByTraverse<eval>, Foldable<eval>,
                                                   Comonad<eval> {

        @Override
        public <T> T foldRight(Monoid<T> monoid,
                               Higher<eval, T> ds) {
            return narrowK(ds).fold(monoid);
        }


        @Override
        public <T> T foldLeft(Monoid<T> monoid,
                              Higher<eval, T> ds) {
            return narrowK(ds).fold(monoid);
        }


        @Override
        public <T, R> Higher<eval, R> flatMap(Function<? super T, ? extends Higher<eval, R>> fn,
                                              Higher<eval, T> ds) {
            return narrowK(ds).flatMap(t -> narrowK(fn.apply(t)));
        }

        @Override
        public <C2, T, R> Higher<C2, Higher<eval, R>> traverseA(Applicative<C2> applicative,
                                                                Function<? super T, ? extends Higher<C2, R>> fn,
                                                                Higher<eval, T> ds) {
            Eval<T> eval = narrowK(ds);
            return applicative.map(Eval::now,
                                   fn.apply(eval.get()));
        }

        @Override
        public <T, R> R foldMap(Monoid<R> mb,
                                Function<? super T, ? extends R> fn,
                                Higher<eval, T> ds) {
            Eval<R> opt = narrowK(ds).map(fn);
            return opt.fold(mb);
        }

        @Override
        public <T, R> Higher<eval, R> ap(Higher<eval, ? extends Function<T, R>> fn,
                                         Higher<eval, T> apply) {
            return narrowK(apply).zip(narrowK(fn),
                                      (a, b) -> b.apply(a));
        }

        @Override
        public <T> Higher<eval, T> unit(T value) {
            return Eval.now(value);
        }

        @Override
        public <T, R> Higher<eval, R> map(Function<? super T, ? extends R> fn,
                                          Higher<eval, T> ds) {
            return narrowK(ds).map(fn);
        }

        @Override
        public <T, R> Higher<eval, R> tailRec(T initial,
                                              Function<? super T, ? extends Higher<eval, ? extends Either<T, R>>> fn) {
            return Eval.tailRec(initial,
                                t -> narrowK(fn.apply(t)));
        }

        @Override
        public <T> Higher<eval, Higher<eval, T>> nest(Higher<eval, T> ds) {
            return Eval.later(() -> ds);
        }

        @Override
        public <T, R> Higher<eval, R> coflatMap(Function<? super Higher<eval, T>, R> mapper,
                                                Higher<eval, T> ds) {
            return Eval.later(() -> mapper.apply(ds));
        }

        @Override
        public <T> T extract(Higher<eval, T> ds) {
            return narrowK(ds).get();
        }
    }


}
