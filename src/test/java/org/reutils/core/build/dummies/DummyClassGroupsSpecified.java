package org.reutils.core.build.dummies;

import org.reutils.annotations.Group;

public class DummyClassGroupsSpecified {

    @Group(group = "v1")
    String value1;

    @Group(group = "test")
    private String value2;

    @Group
    protected String value3;

    @Group
    public String value4;

}
