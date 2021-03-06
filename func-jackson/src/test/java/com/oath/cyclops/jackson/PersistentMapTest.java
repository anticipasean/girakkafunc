package com.oath.cyclops.jackson;

import static org.hamcrest.Matchers.equalTo;

import cyclops.container.HashMap;
import org.junit.Test;

public class PersistentMapTest {

    @Test
    public void hashMap() {
        System.out.println(JacksonUtil.serializeToJson(HashMap.of("a",
                                                                  10)));
        System.out.println(JacksonUtil.serializeToJson(HashMap.empty()));

        assertThat(JacksonUtil.convertFromJson(JacksonUtil.serializeToJson(HashMap.of("a",
                                                                                      10)),
                                               HashMap.class),
                   equalTo(HashMap.of("a",
                                      10)));
    }
}
