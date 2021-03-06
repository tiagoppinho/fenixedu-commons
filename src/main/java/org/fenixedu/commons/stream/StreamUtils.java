package org.fenixedu.commons.stream;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Utility methods to provide a bridge between Java 8 Streams and pre-Java 8 libraries.
 * 
 * @author Joao Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class StreamUtils {

    /**
     * Returns a {@link JsonArray} that results from applying the provided filler to each origin object.

     * @param filler the filler to apply to the origin object to fill the JsonObject
     * @param origin the original objects to be transformed
     * 
     * @return
     *         A {@link JsonArray} that has been filled in by the filler function applied to each origin object.
     */
    public static <T> JsonArray toJsonArray(final BiConsumer<JsonObject, T> filler, final Iterable<T> origins) {
        return toJsonArray(filler, origins.iterator());
    }

    /**
     * Returns a {@link JsonArray} that results from applying the provided filler to each origin object.

     * @param filler the filler to apply to the origin object to fill the JsonObject
     * @param origin the original objects to be transformed
     * 
     * @return
     *         A {@link JsonArray} that has been filled in by the filler function applied to each origin object.
     */
    public static <T> JsonArray toJsonArray(final BiConsumer<JsonObject, T> filler, final Stream<T> origins) {
        return toJsonArray(filler, origins.iterator());
    }

    /**
     * Returns a {@link JsonArray} that results from applying the provided filler to each origin object.

     * @param filler the filler to apply to the origin object to fill the JsonObject
     * @param origin the original objects to be transformed
     * 
     * @return
     *         A {@link JsonArray} that has been filled in by the filler function applied to each origin object.
     */
    public static <T> JsonArray toJsonArray(final BiConsumer<JsonObject, T> filler, final Iterator<T> origins) {
        final JsonArray result = new JsonArray();
        origins.forEachRemaining(s -> result.add(toJson(filler, s)));
        return result;
    }

    /**
     * Returns a {@link JsonObject} that results from applying the provided filler to the origin object.

     * @param filler the filler to apply to the origin object to fill the JsonObject
     * @param origin the original object to be transformed
     * 
     * @return
     *         A {@link JsonObject} that has been filled in by the filler function applied to the origin.
     */
    public static <T> JsonObject toJson(final BiConsumer<JsonObject, T> filler, final T origin) {
         final JsonObject result = new JsonObject();
         filler.accept(result, origin);
         return result;
     }

    /**
     * Returns a {@link Collector} that accumulates all the given {@link JsonElement}s into a new {@link JsonArray}.
     * 
     * @return
     *         A {@link Collector} that accumulates all the given {@link JsonElement}s into a new {@link JsonArray}.
     */
    public static <T extends JsonElement> Collector<T, JsonArray, JsonArray> toJsonArray() {
        return Collector.of(JsonArray::new, (array, element) -> array.add(element), (one, other) -> {
            one.addAll(other);
            return one;
        }, Characteristics.IDENTITY_FINISH);
    }

    /**
     * Returns a sequential {@link Stream} containing all the elements provided by the given {@link JsonArray}.
     * 
     * For convenience, this method can return a stream of any sub-class of {@link JsonElement}, even though the array type
     * provides no information about it. This can cause some issues if the structure of the array in unknown, but can avoid
     * casting stream types when correctly used.
     * 
     * @param array
     *            The source array
     * @return
     *         The new stream
     */
    public static <T extends JsonElement> Stream<T> of(JsonArray array) {
        @SuppressWarnings("unchecked")
        Iterator<T> iterator = (Iterator<T>) array.iterator();
        return StreamSupport.stream(Spliterators.spliterator(iterator, array.size(), 0), false);
    }

}
