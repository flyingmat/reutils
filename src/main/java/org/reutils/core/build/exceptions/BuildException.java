package org.reutils.core.build.exceptions;

public class BuildException extends RuntimeException {

    public BuildException(final String message) {
        super(message);
    }

    public BuildException(final String message, final Exception cause) {
        super(message, cause);
    }

}
