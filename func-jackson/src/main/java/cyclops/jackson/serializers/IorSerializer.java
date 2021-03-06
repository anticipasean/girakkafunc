package cyclops.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cyclops.exception.ExceptionSoftener;
import cyclops.container.control.Ior;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class IorSerializer extends JsonSerializer<Ior<?, ?>> {

    private static final long serialVersionUID = 1L;

    @Override
    public void serialize(Ior<?, ?> value,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {

        value.fold(ExceptionSoftener.softenFunction(l -> {
                       JsonSerializer<Object> ser = serializers.findValueSerializer(LeftBean.class);
                       ser.serialize(new LeftBean(l),
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
                   }),
                   ExceptionSoftener.softenBiFunction((l, r) -> {
                       JsonSerializer<Object> ser = serializers.findValueSerializer(BothBean.class);
                       ser.serialize(new BothBean(l,
                                                  r),
                                     gen,
                                     serializers);
                       return null;
                   }));

    }

    @AllArgsConstructor
    public static class LeftBean {

        @Getter
        @Setter
        private Object left;
    }

    @AllArgsConstructor
    public static class RightBean {

        @Getter
        @Setter
        private Object right;

    }

    @AllArgsConstructor
    public static class BothBean {

        @Getter
        @Setter
        private Object left;
        @Getter
        @Setter
        private Object right;
    }
}
