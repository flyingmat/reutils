package org.reutils.core.build.dummies;

import org.reutils.annotations.Group;

public class DummyClassNoGoodConstructor {

    @Group
    private String value1;

    public DummyClassNoGoodConstructor(final String value1) {
        this.value1 = value1;
    }

}
