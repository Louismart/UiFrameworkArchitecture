package framework.collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import lombok.val;
import mappers.ApiObjectMappers;
import models.codes.gtm.DataLayer;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class WrCollectors {
    private static final String noItemsMessagePattern = "Cannot find %s";
    private static final String multipleItemsMessagePattern = "More than one %s found";
    private static final Function<String, Throwable> DEFAULT_EXCEPTION_SUPPLIER = AssertionError::new;

    public static <T> Collector<T, ?, T> onlyOne(String itemName) {
        return onlyOne(itemName, DEFAULT_EXCEPTION_SUPPLIER);
    }

    public static <T> Collector<T, ?, T> onlyOne(String itemName, Function<String, Throwable> exceptionSupplier) {
        return Collectors.collectingAndThen(
                Collectors.toSet(),
                set -> getSingleValidatedItem(set, itemName, exceptionSupplier));
    }

    /**
     * @return Collector that creates DataLayer object from JsonObject's entrySet()
     */
    public static Collector<Map.Entry<String, JsonElement>, ?, DataLayer> toDataLayer() {
        val mapper = ApiObjectMappers.getBuilderMapper();
        Collector<Map.Entry<String, JsonElement>, ?, DataLayer> result =
                Collectors.collectingAndThen(
                        toJsonObject(),
                        json -> {
                            try {
                                mapper.registerModule(new JavaTimeModule());
                                return mapper.readValue(json.toString(), DataLayer.class);
                            } catch (JsonProcessingException | RuntimeException e) {
                                throw new AssertionError(String.format("Cannot parse dataLayer: %s", e.getMessage()), e);
                            }
                        });
        return result;
    }

    /**
     * Collector that merges down JsonObjects (i.e. objects containing multiple key-value pairs).
     * When duplicated key is found, the old value is being replaced with new one.
     *
     * @return Collector that merges down JsonObjects from JsonObject's entrySet()
     */
    public static Collector<Map.Entry<String, JsonElement>, JsonObject, JsonObject> toJsonObject() {
        return new Collector<Map.Entry<String, JsonElement>, JsonObject, JsonObject>() {
            @Override
            public Supplier<JsonObject> supplier() {
                return JsonObject::new;
            }

            @Override
            public BiConsumer<JsonObject, Map.Entry<String, JsonElement>> accumulator() {
                return (json, entry) -> json.add(entry.getKey(), entry.getValue());
            }

            @Override
            public BinaryOperator<JsonObject> combiner() {
                val result = new JsonObject();
                return (json1, json2) -> {
                    val entrySetOne = json1.entrySet();
                    entrySetOne.addAll(json2.entrySet());
                    entrySetOne.forEach(entry -> result.add(entry.getKey(), entry.getValue()));
                    return result;
                };
            }

            @Override
            public Function<JsonObject, JsonObject> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.IDENTITY_FINISH);
            }
        };
    }

    /**
     * Syntactic-sugar method to be used as a way of resolving key-conflicts when collecting to map.
     */
    public static <T> T mergeOver(T prev, T next) {return next;}

    /**
     * Collects stream into human readable String
     * @see StringManipulationManager#formatAsHumanReadableList(List, String)
     */
    public static <T> Collector<T, ?, T> toHumanReadableList(String lastItemSeparator) {
        return (Collector<T, ?, T>) Collectors.collectingAndThen(
                Collectors.toList(),
                list -> StringManipulationManager.formatAsHumanReadableList(list, lastItemSeparator));
    }

    @SneakyThrows
    private static <T> void verifyNumberOfItems(Set<T> set, String itemName, Function<String, Throwable> exceptionSupplier) {
        if (set.isEmpty()) {
            throw exceptionSupplier.apply(String.format(noItemsMessagePattern, itemName));
        } else if (set.size() > 1) {
            throw exceptionSupplier.apply(String.format(multipleItemsMessagePattern, itemName));
        }
    }

    private static <T> T getSingleValidatedItem(Set<T> set, String itemName, Function<String, Throwable> exceptionSupplier) {
        verifyNumberOfItems(set, itemName, exceptionSupplier);
        return set.stream().findAny().get();
    }
}


