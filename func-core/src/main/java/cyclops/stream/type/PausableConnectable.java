package cyclops.stream.type;

/**
 * A Connectable (Stream already emitting data) that can be paused and unpaused
 *
 * @param <T> Data type of elements in the Stream
 * @author johnmcclean
 */
public interface PausableConnectable<T> extends Connectable<T> {

    /**
     * Unpause this Connectable (restart data production)
     */
    void unpause();

    /**
     * Pause this Connectable (stop it producing data until unpaused)
     */
    void pause();
}
