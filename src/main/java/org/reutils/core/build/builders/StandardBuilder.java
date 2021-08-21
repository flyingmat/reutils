package org.reutils.core.build.builders;

public interface StandardBuilder {
    <T> T as(final Class<T> clazz);
}
