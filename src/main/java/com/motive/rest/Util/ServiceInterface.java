package com.motive.rest.Util;

import java.util.UUID;

public interface ServiceInterface<T> {
    T save(T object);
    void delete(T object);
    T findById(UUID id);
}
