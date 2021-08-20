package org.reutils.core.build.executors;

import org.reutils.core.build.executors.base.AbstractSingleRegexBuildExecutor;
import org.reutils.core.build.exceptions.BuildException;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleRegexBuildExecutor<C> extends AbstractSingleRegexBuildExecutor<C> {

    public SingleRegexBuildExecutor(final Class<C> clazz, final Pattern pattern) {
        super(clazz, pattern);
    }

    public C build(final String input) {
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new BuildException("The provided pattern does not match against the input.");
        } else {
            return AccessController.doPrivileged((PrivilegedAction<C>) () -> processGroupFields(instantiate(), matcher));
        }
    }
}
