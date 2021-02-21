package cyclops.rxjava2.io;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.pure.reactive.collections.immutable.BagX;
import cyclops.pure.reactive.collections.immutable.LinkedListX;
import cyclops.pure.reactive.collections.immutable.OrderedSetX;
import cyclops.pure.reactive.collections.immutable.PersistentQueueX;
import cyclops.pure.reactive.collections.immutable.PersistentSetX;
import cyclops.pure.reactive.collections.immutable.VectorX;
import cyclops.pure.reactive.collections.mutable.DequeX;
import cyclops.reactive.collection.container.mutable.ListX;
import cyclops.pure.reactive.collections.mutable.QueueX;
import cyclops.pure.reactive.collections.mutable.SetX;
import cyclops.pure.reactive.collections.mutable.SortedSetX;
import cyclops.reactive.companion.Spouts;
import cyclops.rxjava2.container.companion.FlowableCollections;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

public class FlowableCollectionsTest {

    Executor ex = Executors.newFixedThreadPool(1);
    AtomicBoolean complete;
    Flowable<Integer> async;

    @Before
    public void setup() {
        complete = new AtomicBoolean(false);
        async = Flowable.fromPublisher(Spouts.reactive(Stream.of(100,
                                                                 100,
                                                                 100),
                                                       ex))
                        .map(i -> {
                            Thread.sleep(500);
                            return i;
                        })
                        .doOnComplete(() -> complete.set(true));
    }

    @Test
    public void listX() {

        System.out.println("Initializing!");
        ListX<Integer> asyncList = FlowableCollections.listX(async)
                                                      .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.get(0);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void listX2() {

        FlowableCollections.listX(Flowable.interval(1,
                                                    TimeUnit.SECONDS,
                                                    Schedulers.io())
                                          .doOnNext(System.out::println)
                                          .take(2)
                                          .doOnComplete(() -> complete.set(true)));

        System.out.println("Blocked? " + complete.get());
        assertFalse(complete.get());
        while (!complete.get()) {

        }
        assertTrue(complete.get());
    }

    @Test
    public void queueX() {

        System.out.println("Initializing!");
        QueueX<Integer> asyncList = FlowableCollections.queueX(async)
                                                       .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.firstValue(-1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void setX() {

        System.out.println("Initializing!");
        SetX<Integer> asyncList = FlowableCollections.setX(async)
                                                     .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.firstValue(-1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void sortedSetX() {

        System.out.println("Initializing!");
        SortedSetX<Integer> asyncList = FlowableCollections.sortedSetX(async)
                                                           .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.firstValue(-1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void dequeX() {

        System.out.println("Initializing!");
        DequeX<Integer> asyncList = FlowableCollections.dequeX(async)
                                                       .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.firstValue(-1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void linkedListX() {

        System.out.println("Initializing!");
        LinkedListX<Integer> asyncList = FlowableCollections.linkedListX(async)
                                                            .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.getOrElse(0,
                                        -1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void vectorX() {

        System.out.println("Initializing!");
        VectorX<Integer> asyncList = FlowableCollections.vectorX(async)
                                                        .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.getOrElse(0,
                                        -1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void persistentQueueX() {

        System.out.println("Initializing!");
        PersistentQueueX<Integer> asyncList = FlowableCollections.persistentQueueX(async)
                                                                 .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.firstValue(-1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void persistentSetX() {

        System.out.println("Initializing!");
        PersistentSetX<Integer> asyncList = FlowableCollections.persistentSetX(async)
                                                               .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.firstValue(-1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void orderedSetX() {

        System.out.println("Initializing!");
        OrderedSetX<Integer> asyncList = FlowableCollections.orderedSetX(async)
                                                            .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.firstValue(-1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }

    @Test
    public void bagX() {

        System.out.println("Initializing!");
        BagX<Integer> asyncList = FlowableCollections.bagX(async)
                                                     .map(i -> i + 1);

        boolean blocked = complete.get();
        System.out.println("Blocked? " + blocked);
        assertFalse(complete.get());

        int value = asyncList.firstValue(-1);

        System.out.println("First value is " + value);
        assertThat(value,
                   equalTo(101));

        System.out.println("Blocked? " + complete.get());
        assertTrue(complete.get());
    }
}