package org.reutils.core.build.dummies;

import org.reutils.annotations.Group;

public class DummyClassFunctionInteger {

    @Group
    private String string;

    @Group(function = "toInt")
    private Integer mapthis;

}
