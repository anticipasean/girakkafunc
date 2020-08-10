package cyclops.instances.jdk;

import cyclops.function.higherkinded.DataWitness.predicate;
import cyclops.function.higherkinded.Higher;
import cyclops.kinds.PredicateKind;
import cyclops.typeclasses.functor.ContravariantFunctor;
import java.util.function.Function;

public interface PredicateInstances {

    static ContravariantFunctor<predicate> contravariantFunctor() {
        return new ContravariantFunctor<predicate>() {
            @Override
            public <T, R> Higher<predicate, R> contramap(Function<? super R, ? extends T> fn,
                                                         Higher<predicate, T> ds) {
                PredicateKind<R> r = in -> PredicateKind.narrow(ds)
                                                        .test(fn.apply(in));
                return r;
            }
        };
    }
}
