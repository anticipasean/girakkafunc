package cyclops.pure.instances.jdk.transformers;

import cyclops.function.higherkinded.DataWitness.optional;
import cyclops.function.higherkinded.Higher;
import cyclops.pure.container.functional.Nested;
import cyclops.pure.kinds.OptionalKind;
import cyclops.pure.transformers.Transformer;
import cyclops.pure.transformers.TransformerFactory;
import cyclops.pure.typeclasses.monad.Monad;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalTransformer<W1, T> implements Transformer<W1, optional, T> {

    private final Nested<W1, optional, T> nested;
    private final Monad<W1> monad1;

    private final static <W1> TransformerFactory<W1, optional> factory() {
        return OptionalTransformer::optionalT;
    }

    public static <W1, T> OptionalTransformer<W1, T> optionalT(Nested<W1, optional, T> nested) {
        return new OptionalTransformer<W1, T>(nested,
                                              nested.def1.monad());
    }

    @Override
    public <R> Nested<W1, optional, R> flatMap(Function<? super T, ? extends Nested<W1, optional, R>> fn) {
        Higher<W1, Higher<optional, R>> r = monad1.flatMap(m -> OptionalKind.narrow(m)
                                                                            .map(t -> fn.apply(t).nested)
                                                                            .orElseGet(() -> monad1.unit(OptionalKind.empty())),
                                                           nested.nested);

        return Nested.of(r,
                         nested.def1,
                         nested.def2);


    }

    @Override
    public <R> Nested<W1, optional, R> flatMapK(Function<? super T, ? extends Higher<W1, Higher<optional, R>>> fn) {
        return flatMap(fn.andThen(x -> Nested.of(x,
                                                 nested.def1,
                                                 nested.def2)));
    }


}
