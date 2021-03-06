package cyclops.container.factory;

/**
 * Represents a type that can be instantiated in zero form.
 *
 * @param <T> Data type of element(s) storeable within this EmptyUnit
 * @author johnmcclean
 */
public interface EmptyUnit<T> extends Unit<T> {

    /**
     * @return A new, zero instance of this EmptyUnit type
     */
    <T> Unit<T> emptyUnit();
}
