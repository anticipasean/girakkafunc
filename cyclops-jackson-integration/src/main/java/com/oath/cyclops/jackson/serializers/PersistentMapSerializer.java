package com.oath.cyclops.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import cyclops.container.persistent.PersistentMap;
import cyclops.container.tuple.Tuple2;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PersistentMapSerializer extends JsonSerializer<PersistentMap<?, ?>> {

    private static final long serialVersionUID = 1L;


    @Override
    public void serialize(PersistentMap<?, ?> value,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {

        if (value.iterator()
                 .hasNext()) {
            Tuple2<?, ?> keyAndValue = value.iterator()
                                            .next();
            MapType type = TypeFactory.defaultInstance()
                                      .constructMapType(Map.class,
                                                        keyAndValue._1()
                                                                   .getClass(),
                                                        keyAndValue._2()
                                                                   .getClass());
            serializers.findTypedValueSerializer(type,
                                                 true,
                                                 null)
                       .serialize(value.mapView(),
                                  gen,
                                  serializers);
        } else {
            serializers.findValueSerializer(Map.class)
                       .serialize(new HashMap<>(),
                                  gen,
                                  serializers);
        }


    }
}
