package cyclops.function.combiner;

import cyclops.function.enhanced.BinaryFunction;
import java.util.function.BinaryOperator;

/**
 * An (associative) binary operation for combining values. Implementations should obey associativity laws.
 *
 * @param <T> Data type of elements to be combined
 * @author johnmcclean
 */
@FunctionalInterface
public interface Semigroup<T> extends BinaryFunction<T>, BinaryOperator<T> {

    /* (non-Javadoc)
     * @see java.util.function.BiFunction#applyHKT(java.lang.Object, java.lang.Object)
     */
    @Override
    T apply(T t,
            T u);

}
