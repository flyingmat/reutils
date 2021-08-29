package org.reutils.core.build.builders;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipleRegexBuilder extends AbstractRegexBuilder {

    private final Collection<Pattern> patterns;

    public MultipleRegexBuilder(final Collection<Pattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    public <T> Optional<T> build(final String input, final Class<T> clazz) {
        return patterns.stream().map(p -> p.matcher(input)).filter(Matcher::matches).findFirst().map(m -> processGroupFields(instantiate(clazz), m));
    }

    public Collection<Pattern> getPatterns() {
        return this.patterns;
    }

}
