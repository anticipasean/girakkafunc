package cyclops.reactive.companion;

import com.oath.cyclops.data.collections.extensions.lazy.LazyDequeX;
import com.oath.cyclops.data.collections.extensions.lazy.LazyListX;
import com.oath.cyclops.data.collections.extensions.lazy.LazyQueueX;
import com.oath.cyclops.data.collections.extensions.lazy.LazySetX;
import com.oath.cyclops.data.collections.extensions.lazy.LazySortedSetX;
import cyclops.function.evaluation.Evaluation;
import cyclops.reactive.collections.mutable.DequeX;
import cyclops.reactive.collections.mutable.ListX;
import cyclops.reactive.collections.mutable.QueueX;
import cyclops.reactive.collections.mutable.SetX;
import cyclops.reactive.collections.mutable.SortedSetX;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collector;


/**
 * CyclopsCollectors for Cyclops Extended Collections
 *
 * @author johnmcclean
 */
public interface CyclopsCollectors {

    /**
     * @return Collector to QueueX
     */
    static <T> Collector<T, ?, QueueX<T>> toQueueX() {
        return java.util.stream.Collectors.collectingAndThen(QueueX.defaultCollector(),
                                                             (final Queue<T> d) -> new LazyQueueX<>(d,
                                                                                                    QueueX.defaultCollector(),
                                                                                                    Evaluation.LAZY));

    }

    /**
     * @return Collector for Seq
     */
    static <T> Collector<T, ?, ListX<T>> toListX() {
        return java.util.stream.Collectors.collectingAndThen(ListX.defaultCollector(),
                                                             (final List<T> d) -> new LazyListX<T>(d,
                                                                                                   null,
                                                                                                   ListX.defaultCollector(),
                                                                                                   Evaluation.LAZY));

    }

    /**
     * @return Collector for DequeX
     */
    static <T> Collector<T, ?, DequeX<T>> toDequeX() {
        return java.util.stream.Collectors.collectingAndThen(DequeX.defaultCollector(),
                                                             (final Deque<T> d) -> new LazyDequeX<>(d,
                                                                                                    DequeX.defaultCollector(),
                                                                                                    Evaluation.LAZY));

    }

    /**
     * @return Collector for SetX
     */
    static <T> Collector<T, ?, SetX<T>> toSetX() {
        return java.util.stream.Collectors.collectingAndThen(SetX.defaultCollector(),
                                                             (final Set<T> d) -> new LazySetX<>(d,
                                                                                                SetX.defaultCollector(),
                                                                                                Evaluation.LAZY));
    }

    /**
     * @return Collector for SortedSetX
     */
    static <T> Collector<T, ?, SortedSetX<T>> toSortedSetX() {
        return java.util.stream.Collectors.collectingAndThen(SortedSetX.defaultCollector(),
                                                             (final SortedSet<T> d) -> new LazySortedSetX<>(d,
                                                                                                            SortedSetX.defaultCollector(),
                                                                                                            Evaluation.LAZY));

    }
}
