package org.reutils.core.build.dummies;

import org.reutils.annotations.Group;

public class DummyClassGroupsNumbered {

    @Group(group = "$1")
    String value1;

    @Group(group = "\\2")
    private String value2;

    @Group
    protected String value3;

    @Group
    public String value4;

}
