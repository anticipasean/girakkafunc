package io.github.anticipasean.ent.iterator;

import cyclops.container.control.Try;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TypeMatchingIterableTest {

    @Test
    public void createTypeMatchingIterableNegativeMatchTest() {
        Iterator<Integer> iterator = Stream.of(1,
                                               3,
                                               5)
                                           .iterator();
        Iterable iterable = () -> iterator;
        Iterable<Float> floatIterable = TypeMatchingIterable.of(iterable.iterator(),
                                                                Float.class);
        Assert.assertFalse(floatIterable.iterator()
                                        .hasNext());
    }

    @Test
    public void createTypeMatchingIterablePositiveMatchTest() {
        Iterator<Integer> iterator = Stream.of(1,
                                               3,
                                               5)
                                           .iterator();
        Iterable iterable = () -> iterator;
        Iterable<Integer> integerIterable = TypeMatchingIterable.of(iterable.iterator(),
                                                                    Integer.class);
        Assert.assertTrue(integerIterable.iterator()
                                         .hasNext());
    }

    @Test
    public void typeMatchingIterableRequeryableTest() {
        Iterator<Integer> iterator = Stream.of(1,
                                               3,
                                               5)
                                           .iterator();
        Iterable iterable = () -> iterator;
        Iterable<Integer> integerIterable = TypeMatchingIterable.of(iterable.iterator(),
                                                                    Integer.class);
        Assert.assertTrue(integerIterable.iterator()
                                         .hasNext());

        Assert.assertTrue(integerIterable.iterator()
                                         .hasNext());

    }

    @Test
    public void typeMatchingIterableReplayableTest() {
        Iterator<Integer> iterator = Stream.of(1,
                                               3,
                                               5)
                                           .iterator();
        Iterable iterable = () -> iterator;
        Iterable<Integer> integerIterable = TypeMatchingIterable.of(iterable.iterator(),
                                                                    Integer.class);
        Iterator<Integer> integerIterator1 = integerIterable.iterator();
        Iterator<Integer> integerIterator2 = integerIterable.iterator();
        Assert.assertEquals(integerIterator1.next(),
                            Integer.valueOf(1));
        Assert.assertEquals(integerIterator1.next(),
                            Integer.valueOf(3));
        Assert.assertEquals(integerIterator2.next(),
                            Integer.valueOf(1),
                            "iterable not replayable as expected");
    }

    @Test
    public void typeMatchingIterableRunThroughTest() {
        Iterator<Integer> iterator = Stream.of(1,
                                               3,
                                               5)
                                           .iterator();
        Iterable iterable = () -> iterator;
        Iterable<Integer> integerIterable = TypeMatchingIterable.of(iterable.iterator(),
                                                                    Integer.class);
        Iterator<Integer> integerIterator1 = integerIterable.iterator();
        Assert.assertEquals(integerIterator1.next(),
                            Integer.valueOf(1));
        Assert.assertEquals(integerIterator1.next(),
                            Integer.valueOf(3));
        Assert.assertEquals(integerIterator1.next(),
                            Integer.valueOf(5));
        Try<Integer, NoSuchElementException> iterTry = Try.withCatch(() -> integerIterator1.next(),
                                                                     NoSuchElementException.class);
        Assert.assertTrue(iterTry.isFailure(),
                          "iterator did not throw expected NoSuchElementException when next called after last element.");
    }
}
