package com.oath.cyclops.jackson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import cyclops.container.control.Eval;
import cyclops.container.control.Future;
import org.junit.Ignore;
import org.junit.Test;

public class FutureTest {

    Future<Integer> some = Future.ofResult(10);

    @Test
    @Ignore
    public void roundTrip() {

        String json = JacksonUtil.serializeToJson(Eval.now(10));
        System.out.println("Json " + json);
        Future<Integer> des = JacksonUtil.convertFromJson(json,
                                                          Future.class);

        assertThat(des,
                   equalTo(some));
    }

    @Test
    public void some() {
        assertThat(JacksonUtil.serializeToJson(Future.ofResult(5)),
                   equalTo("5"));
    }


}
