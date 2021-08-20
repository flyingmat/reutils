package org.reutils.core.build.executors.base;

import java.util.regex.Pattern;

public abstract class AbstractSingleRegexBuildExecutor<C> extends AbstractBuildExecutor<C> {

    protected final Pattern pattern;

    public AbstractSingleRegexBuildExecutor(final Class<C> clazz, final Pattern pattern) {
        super(clazz);
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

}
