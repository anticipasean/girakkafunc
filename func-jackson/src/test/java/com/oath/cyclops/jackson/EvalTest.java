package com.oath.cyclops.jackson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import cyclops.container.control.Eval;
import org.junit.Test;

public class EvalTest {

    Eval<Integer> some = Eval.now(10);

    @Test
    public void roundTrip() {

        String json = JacksonUtil.serializeToJson(Eval.now(10));
        System.out.println("Json " + json);
        Eval<Integer> des = JacksonUtil.convertFromJson(json,
                                                        Eval.class);

        assertThat(des,
                   equalTo(some));
    }

    @Test
    public void some() {
        assertThat(JacksonUtil.serializeToJson(Eval.now(5)),
                   equalTo("5"));
    }


}
