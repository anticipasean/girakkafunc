package cyclops.async.queue;

import cyclops.async.queue.Queue;
import cyclops.async.queue.Topic;
import cyclops.container.control.Either;
import cyclops.container.foldable.Sealed2;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.subscription.Continueable;
import cyclops.stream.async.Continuation;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Interface for an Adapter that inputs data from 1 or more input Streams and sends it to 1 or more emitted Streams
 *
 * @param <T> Data type
 * @author johnmcclean
 */
public interface Adapter<T> extends Sealed2<Queue<T>, Topic<T>> {

    void addContinuation(Continuation cont);

    /**
     * @return A structural Pattern Matcher for this Adapter that allows matching on  Queue / Topic types
     */
    default Either<Queue<T>, Topic<T>> matches() {
        return fold(q -> Either.left(q),
                    topic -> Either.right(topic));
    }

    /**
     * Conditionally execute one of the supplied function depending on whether or not this Adapter is a Queue or a Topic
     *
     * @param caseQueue Function to execute if this Adapter is a Queue
     * @param caseTopic Function to execute if this Adapter is a Topic
     * @return Value returned from executed funciton
     */
    <R> R fold(Function<? super Queue<T>, ? extends R> caseQueue,
               Function<? super Topic<T>, ? extends R> caseTopic);

    /**
     * Offer a single datapoint to this adapter
     *
     * @param data data to add
     * @return self
     */
    boolean offer(T data);

    /**
     * @param stream Input data from provided Stream
     */
    boolean fromStream(Stream<T> stream);


    /**
     * @return Stream of data
     */
    ReactiveSeq<T> stream();

    /**
     * @return Stream of data
     */
    ReactiveSeq<T> stream(Continueable s);

    /**
     * @return Stream of CompletableFutures that can be used as input into a SimpleReact concurrent dataflow
     */
    ReactiveSeq<CompletableFuture<T>> streamCompletableFutures();

    /**
     * Close this adapter
     *
     * @return true if closed
     */
    boolean close();
}
