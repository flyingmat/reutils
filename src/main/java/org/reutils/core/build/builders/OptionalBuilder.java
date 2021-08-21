package org.reutils.core.build.builders;

import java.util.Optional;

public interface OptionalBuilder {
    <T> Optional<T> asOptional(final Class<T> clazz);
}
