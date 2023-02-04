package com.motive.rest.Util;

public interface ServiceInterface<T> {
    T save(T object);
    boolean delete(T object);
    boolean findById(T id);
}
