package mappers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import lombok.val;

import java.util.Arrays;
import java.util.Optional;

public class ApiObjectMappers {
    private static ObjectMapper defaultObjectMapper;

    public static synchronized ObjectMapper getDefaultObjectMapper() {
        return Optional.ofNullable(defaultObjectMapper).orElseGet(() -> {
            defaultObjectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                    .findAndRegisterModules();
            return defaultObjectMapper;
        });
    }

    /**
     * To jackson-deserialize a @Value object with @Builder, the object must be:
     * - annotated with @JsonDeserialize(builder = ...)
     * - all fields must be annotated with @JsonProperty (even if they are of the same name), OR
     * - empty builder class must be annotated with @JsonPOJOBuilder(withPrefix = "")
     *
     * Thanks to this mapper, the object no longer needs to meet the above requirements.
     *
     * @param ignoringUnknown if true, returned mapper will ignore unknown properties
     *
     * @return mapper with lombok builder support
     */
    public static ObjectMapper getBuilderMapper(boolean ignoringUnknown) {
        val mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
                if (ac.hasAnnotation(JsonPOJOBuilder.class)) {  // if no annotation present use default as empty prefix
                    return super.findPOJOBuilderConfig(ac);
                }
                return new JsonPOJOBuilder.Value("build", "");
            }

            @Override
            public JsonIgnoreProperties.Value findPropertyIgnoralByName(final MapperConfig<?> config, final Annotated a) {
                if (ignoringUnknown) {
                    return JsonIgnoreProperties.Value.forIgnoreUnknown(true);
                }
                return super.findPropertyIgnoralByName(config, a);
            }

            @Override
            public Class<?> findPOJOBuilder(AnnotatedClass ac) {
                JsonDeserialize ann = _findAnnotation(ac, JsonDeserialize.class);
                val builderClass = Arrays.stream(ac.getAnnotated().getClasses())
                        .filter(cls -> cls.getName().endsWith("Builder"))
                        .findFirst()
                        .orElse(null);
                return (ann == null) ? builderClass : _classIfExplicit(ann.builder());
            }
        });
        return mapper;
    }

    public static ObjectMapper getBuilderMapper() {
        return getBuilderMapper(false);
    }
}

