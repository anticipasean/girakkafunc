package cyclops.async.reactive.futurestream.pipeline;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.agrona.concurrent.ManyToOneConcurrentArrayQueue;

/**
 * Active consumer / multiple producer future pool
 *
 * @author johnmcclean
 */
@AllArgsConstructor
public class FuturePool {

    private final ManyToOneConcurrentArrayQueue<FastFuture> pool;
    private final int max;

    public <T> FastFuture<T> next(final Supplier<FastFuture<T>> factory) {
        if (pool.size() > 0) {

            final FastFuture next = pool.poll();
            next.clearFast();
            return next;
        }

        return factory.get();
    }

    public <T> void done(final FastFuture<T> f) {
        if (pool.size() < max) {

            pool.offer(f);
        }

    }
}
