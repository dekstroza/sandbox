package io.dekstroza.domain;

public interface BaseEntity<T> {

    T getId();

    void setId(T t);

}
