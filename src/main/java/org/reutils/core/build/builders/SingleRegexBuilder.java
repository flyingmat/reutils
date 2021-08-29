package org.reutils.core.build.builders;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleRegexBuilder extends AbstractRegexBuilder {

    private final Pattern pattern;

    public SingleRegexBuilder(final Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public <T> Optional<T> build(final String input, final Class<T> clazz) {
        final Matcher matcher = pattern.matcher(input);
        return matcher.matches() ? Optional.of(processGroupFields(instantiate(clazz), matcher)) : Optional.empty();
    }

    public Pattern getPattern() {
        return this.pattern;
    }

}
