package org.reutils.core.build.executors.base;

import java.util.Collection;
import java.util.regex.Pattern;

public abstract class AbstractMultipleRegexBuildExecutor<C> extends AbstractBuildExecutor<C> {

    protected final Collection<Pattern> patterns;

    public AbstractMultipleRegexBuildExecutor(final Class<C> clazz, final Collection<Pattern> patterns) {
        super(clazz);
        this.patterns = patterns;
    }

    public Collection<Pattern> getPatterns() {
        return patterns;
    }

}
