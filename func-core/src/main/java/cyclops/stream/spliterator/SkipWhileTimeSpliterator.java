package cyclops.stream.spliterator;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by johnmcclean on 22/12/2016.
 */
public class SkipWhileTimeSpliterator<T> extends Spliterators.AbstractSpliterator<T> implements CopyableSpliterator<T> {

    final long toRun;
    private final Spliterator<T> source;
    private final long time;
    private final TimeUnit t;
    boolean closed = false;
    boolean open = false;
    long start = -1;

    public SkipWhileTimeSpliterator(final Spliterator<T> source,
                                    long time,
                                    TimeUnit t) {
        super(source.estimateSize(),
              source.characteristics() & Spliterator.ORDERED);

        this.source = source;
        this.time = time;
        this.t = t;

        toRun = t.toNanos(time);

    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        start = System.nanoTime();
        while (!closed) {
            boolean canAdvance = source.tryAdvance(t -> {
                if (!open) {
                    open = System.nanoTime() - start >= toRun;

                    if (open) {
                        action.accept(t);
                    }
                } else {
                    action.accept(t);
                }

            });
            if (!canAdvance) {
                closed = true;
                return;
            }

        }


    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (closed) {
            return true;
        }
        if (start == -1) {
            start = System.nanoTime();
        }

        boolean[] sent = {false};
        for (; ; ) {
            boolean canAdvance = source.tryAdvance(t -> {
                if (!open) {
                    open = System.nanoTime() - start >= toRun;

                    if (open) {
                        action.accept(t);
                        sent[0] = true;
                    }
                } else {
                    action.accept(t);
                    sent[0] = true;
                }
            });

            if (!canAdvance) {
                closed = true;
                return false;
            }
            if (sent[0]) {
                return canAdvance;
            }
        }

    }

    @Override
    public Spliterator<T> copy() {
        return new SkipWhileTimeSpliterator<>(CopyableSpliterator.copy(source),
                                              time,
                                              t);
    }
}
