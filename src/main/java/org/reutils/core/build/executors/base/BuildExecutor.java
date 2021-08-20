package org.reutils.core.build.executors.base;

import org.reutils.core.build.exceptions.BuildException;

public interface BuildExecutor<C> {
    C build(final String input) throws BuildException;
}
