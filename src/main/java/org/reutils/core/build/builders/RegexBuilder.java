package org.reutils.core.build.builders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

public interface RegexBuilder {

    <T> Optional<T> build(final String input, final Class<T> clazz);

    static SingleRegexBuilder of(final Pattern pattern) {
        return new SingleRegexBuilder(pattern);
    }

    static SingleRegexBuilder of(final String pattern) {
        return new SingleRegexBuilder(Pattern.compile(pattern));
    }

    static MultipleRegexBuilder of(final Collection<Pattern> patterns) {
        return new MultipleRegexBuilder(patterns);
    }

    static MultipleRegexBuilder of(final Pattern... patterns) {
        return new MultipleRegexBuilder(Arrays.asList(patterns));
    }

    static MultipleRegexBuilder of(final String... patterns) {
        return new MultipleRegexBuilder(Arrays.stream(patterns).map(Pattern::compile).toList());
    }

}
