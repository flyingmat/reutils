package org.reutils.core.build;

import org.reutils.core.build.executors.MultipleRegexBuildExecutor;
import org.reutils.core.build.executors.SingleRegexBuildExecutor;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class ReBuilder {

    public static <C> SingleRegexBuildExecutor<C> of(final Class<C> clazz, final Pattern pattern) {
        return new SingleRegexBuildExecutor<>(clazz, pattern);
    }

    public static <C> SingleRegexBuildExecutor<C> of(final Class<C> clazz, final String pattern) {
        return new SingleRegexBuildExecutor<>(clazz, Pattern.compile(pattern));
    }

    public static <C> MultipleRegexBuildExecutor<C> of(final Class<C> clazz, final Collection<Pattern> patterns) {
        return new MultipleRegexBuildExecutor<>(clazz, patterns);
    }

    public static <C> MultipleRegexBuildExecutor<C> of(final Class<C> clazz, final Pattern... patterns) {
        return new MultipleRegexBuildExecutor<>(clazz, Arrays.asList(patterns));
    }

    public static <C> MultipleRegexBuildExecutor<C> of(final Class<C> clazz, final String... patterns) {
        return new MultipleRegexBuildExecutor<>(clazz, Arrays.stream(patterns).map(Pattern::compile).toList());
    }

}
