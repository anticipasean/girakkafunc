package cyclops.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cyclops.container.foldable.Sealed3;
import cyclops.exception.ExceptionSoftener;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Sealed3Serializer extends JsonSerializer<Sealed3<?, ?, ?>> {

    private static final long serialVersionUID = 1L;

    @Override
    public void serialize(Sealed3<?, ?, ?> value,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {

        value.fold(ExceptionSoftener.softenFunction(l1 -> {
                       JsonSerializer<Object> ser = serializers.findValueSerializer(Left1Bean.class);
                       ser.serialize(new Left1Bean(l1),
                                     gen,
                                     serializers);
                       return null;
                   }),
                   ExceptionSoftener.softenFunction(l2 -> {
                       JsonSerializer<Object> ser = serializers.findValueSerializer(Left2Bean.class);
                       ser.serialize(new Left2Bean(l2),
                                     gen,
                                     serializers);
                       return null;
                   }),
                   ExceptionSoftener.softenFunction(r -> {
                       JsonSerializer<Object> ser = serializers.findValueSerializer(RightBean.class);
                       ser.serialize(new RightBean(r),
                                     gen,
                                     serializers);
                       return null;
                   }));

    }

    @AllArgsConstructor
    public static class Left1Bean {

        @Getter
        @Setter
        private Object left1;
    }

    @AllArgsConstructor
    public static class Left2Bean {

        @Getter
        @Setter
        private Object left2;
    }

    @AllArgsConstructor
    public static class RightBean {

        @Getter
        @Setter
        private Object right;

    }
}
