package org.reutils.core.build.builders;

import org.reutils.core.build.exceptions.BuildException;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleRegexBuilder extends RegexBuilder {

    private final Pattern pattern;

    private SingleRegexBuilder(final Map<String,Function<String,?>> mappings, final String input, final Pattern pattern) {
        super(mappings, input);
        this.pattern = pattern;
    }

    public static class Builder extends RegexBuilder.Builder {

        private final Pattern pattern;

        protected Builder(final Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public SingleRegexBuilder build(final String input) {
            return new SingleRegexBuilder(mappings, input, pattern);
        }

        @Override
        public Builder self() {
            return this;
        }
    }

    @Override
    public <T> T as(final Class<T> clazz) {
        final Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new BuildException("The provided pattern does not match against the input.");
        } else {
            return AccessController.doPrivileged((PrivilegedAction<T>) () -> processGroupFields(instantiate(clazz), matcher));
        }
    }

    @Override
    public <T> Optional<T> asOptional(Class<T> clazz) {
        final Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(AccessController.doPrivileged((PrivilegedAction<T>) () -> processGroupFields(instantiate(clazz), matcher)));
            } catch (final Exception e) {
                return Optional.empty();
            }
        }
    }

}
