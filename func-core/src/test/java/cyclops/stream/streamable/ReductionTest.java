package cyclops.stream.streamable;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.function.companion.Reducers;
import cyclops.stream.type.Streamable;
import org.junit.Test;


public class ReductionTest {

    @Test
    public void reduceWithMonoid() {

        assertThat(Streamable.of("hello",
                                 "2",
                                 "world",
                                 "4")
                             .foldMap(Reducers.toCountInt()),
                   equalTo(4));
    }

    @Test
    public void reduceWithMonoid2() {

        assertThat(Streamable.of("replaceWith",
                                 "two",
                                 "three",
                                 "four")
                             .foldMap(this::toInt,
                                      Reducers.toTotalInt()),
                   equalTo(10));
    }

    int toInt(String s) {
        if ("replaceWith".equals(s)) {
            return 1;
        }
        if ("two".equals(s)) {
            return 2;
        }
        if ("three".equals(s)) {
            return 3;
        }
        if ("four".equals(s)) {
            return 4;
        }
        return -1;
    }

    @Test
    public void reduceWithMonoidJoin() {
        Streamable.of("hello",
                      "2",
                      "world",
                      "4")
                  .join(",");
        assertThat(Streamable.of("hello",
                                 "2",
                                 "world",
                                 "4")
                             .reduce(Reducers.toString(",")),
                   equalTo(",hello,2,world,4"));
    }


}
