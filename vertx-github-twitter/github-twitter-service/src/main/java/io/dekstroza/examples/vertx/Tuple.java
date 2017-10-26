package io.dekstroza.examples.vertx;

import io.vertx.core.json.Json;

public class Tuple<K, V> {
    final K first;
    final V second;

    public Tuple(K first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return Json.encodePrettily(this);
    }
}