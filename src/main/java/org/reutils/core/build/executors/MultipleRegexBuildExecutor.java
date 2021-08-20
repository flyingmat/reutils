package org.reutils.core.build.executors;

import org.reutils.core.build.executors.base.AbstractMultipleRegexBuildExecutor;
import org.reutils.core.build.exceptions.BuildException;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipleRegexBuildExecutor<C> extends AbstractMultipleRegexBuildExecutor<C> {

    public MultipleRegexBuildExecutor(final Class<C> clazz, final Collection<Pattern> patterns) {
        super(clazz, patterns);
    }

    public C build(final String input) {
        return AccessController.doPrivileged((PrivilegedAction<C>) () -> {
            for (final Pattern pattern : patterns) {
                final Matcher matcher = pattern.matcher(input);
                if (matcher.matches()) {
                    return processGroupFields(instantiate(), matcher);
                }
            }

            throw new BuildException("None of the provided patterns match against the input.");
        });
    }
}
