package org.reutils.core.build.dummies;

import org.reutils.annotations.Group;

public class DummyClassOptionalGroup {

    @Group
    private String value1;

    @Group(optional = true)
    private String value2;

}
