package com.bmisiek.structures;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IteratorExtension {
    public static <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
