package cyclops.pure.typeclasses.functor;

import cyclops.function.higherkinded.Higher;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Compose two functors so operations are applied to the inner functor
 * <p>
 * e.g.  given an Optional containing a List we can compose the functor for Optionals and the functor for List to transform the
 * values inside the List
 *
 * <pre>
 * {@code
 *    OptionalType<ListX<Integer>> nest;
 *
 *    Compose.compose(Optionals.functor(),Lists.functor())
 *           .map(i->i*2,nest);
 *
 * }
 * </pre>
 *
 * @param <CRE>
 * @param <C2>
 * @author johnmcclean
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Compose<CRE, C2> {

    private final Functor<CRE> f;
    private final Functor<C2> g;


    /**
     * Compose two functors
     *
     * @param f First functor to compose
     * @param g Second functor to compose
     * @return Composed functor
     */
    public static <CRE, C2> Compose<CRE, C2> compose(Functor<CRE> f,
                                                     Functor<C2> g) {
        return new Compose<>(f,
                             g);
    }

    /**
     * Transformation operation
     *
     * @param fn Transformation function
     * @param ds Datastructure to transform
     * @return Transformed data structure
     */
    public <T, R> Higher<CRE, Higher<C2, R>> map(Function<? super T, ? extends R> fn,
                                                 Higher<CRE, Higher<C2, T>> ds) {
        return f.map(h -> g.map(fn,
                                h),
                     ds);
    }

    public <T> Higher<CRE, Higher<C2, T>> peek(Consumer<? super T> fn,
                                               Higher<CRE, Higher<C2, T>> ds) {
        return map(t -> {
                       fn.accept(t);
                       return t;
                   },
                   ds);
    }

    public <T, R> Higher<CRE, R> map1(Function<? super T, ? extends R> fn,
                                      Higher<CRE, T> ds) {
        return f.map(fn,
                     ds);
    }


    public Functor<CRE> outer() {
        return f;
    }

    public Functor<C2> inter() {
        return g;
    }
}
