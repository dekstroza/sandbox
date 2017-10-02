package io.dekstroza.github.examples.reactive.github;

public class Tuple<K,V>{
    final K first;
    final V second;

    public Tuple(K first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                   "first='" + first + '\'' +
                   ", second='" + second + '\'' +
                   '}';
    }
}