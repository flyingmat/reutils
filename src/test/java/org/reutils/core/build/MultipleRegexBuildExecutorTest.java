package org.reutils.core.build;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reutils.core.build.dummies.*;
import org.reutils.core.build.exceptions.BuildException;
import org.reutils.core.build.executors.MultipleRegexBuildExecutor;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MultipleRegexBuildExecutorTest {

    @ParameterizedTest
    @MethodSource(value = "_buildSuccess")
    public <C> void buildSuccess(final Class<C> clazz, final String input, final Collection<Pattern> patterns) {
        MultipleRegexBuildExecutor<C> classUnderTest = new MultipleRegexBuildExecutor<>(clazz, patterns);

        C result = classUnderTest.build(input);

        assertThat(result).hasFieldOrPropertyWithValue("value1", "v1");
        assertThat(result).hasFieldOrPropertyWithValue("value2", "v2");
        assertThat(result).hasFieldOrPropertyWithValue("value3", "v3");
        assertThat(result).hasFieldOrPropertyWithValue("value4", "v4");
    }

    private static Stream<Arguments> _buildSuccess() {
        final String input = "v1 is the first value, then v2, v3 and v4";
        return Stream.of(
                Arguments.of(
                        DummyClass.class,
                        input,
                        Arrays.asList(
                                Pattern.compile("^THE FIRST value is: (?<value1>.*); THE SECOND value is: (?<value2>.*); THE THIRD value is: (?<value3>.*); THE SECOND value is: (?<value4>.*)$"),
                                Pattern.compile("^(?<value1>.*) is the first value, then (?<value2>.*), (?<value3>.*) and (?<value4>.*)$"))),
                Arguments.of(
                        DummyClassGroupsSpecified.class,
                        input,
                        Arrays.asList(
                                Pattern.compile("^DOESNOTMATCH$"),
                                Pattern.compile("I DONT MATCH (?<v1>EITHER)"),
                                Pattern.compile("^(?<v1>.*) is the first value, then (?<test>.*), (?<value3>.*) and (?<value4>.*)$"),
                                Pattern.compile("^$"))),
                Arguments.of(
                        DummyClassGroupsNumbered.class,
                        "v1 is the first value, then v2, v3, v4",
                        Arrays.asList(
                                Pattern.compile("^(?<v1>.*) is the first value, then (?<test>.*), (?<value3>.*) and (?<value4>.*)$"),
                                Pattern.compile("^(?<v1>.*) is the first value, then (?<test>.*), (?<value3>.*), (?<value4>.*)$")))
        );
    }

    @Test
    public void buildDoesNotMatchAny() {
        MultipleRegexBuildExecutor<DummyClass> classUnderTest =
                new MultipleRegexBuildExecutor<>(DummyClass.class, Arrays.asList(Pattern.compile("^$"), Pattern.compile("^DOESNOTMATCH(.*)$")));

        assertThatThrownBy(() -> classUnderTest.build("fail")).isInstanceOf(BuildException.class)
                .hasMessage("None of the provided patterns match against the input.");
    }

    @Test
    public void buildGroupNotFound() {
        MultipleRegexBuildExecutor<DummyClass> classUnderTest =
                new MultipleRegexBuildExecutor<>(DummyClass.class, Arrays.asList(Pattern.compile("NEXTPATTERNMATCHES"), Pattern.compile("^(?<fail>.*)$")));

        assertThatThrownBy(() -> classUnderTest.build("test")).isInstanceOf(BuildException.class)
                .hasMessageMatching("^Unable to set field 'value\\d'\\.$")
                .getCause().isInstanceOf(IllegalArgumentException.class).hasMessageMatching("^No group with name <value\\d>$");
    }

    @Test
    public void buildNoArgumentsConstructorNotFound() {
        MultipleRegexBuildExecutor<DummyClassNoGoodConstructor> classUnderTest =
                new MultipleRegexBuildExecutor<>(DummyClassNoGoodConstructor.class, Arrays.asList(Pattern.compile("NEXTPATTERNMATCHES"), Pattern.compile("^(?<fail>.*)$")));

        assertThatThrownBy(() -> classUnderTest.build("test")).isInstanceOf(BuildException.class)
                .hasMessage("Unable to find a constructor taking no arguments in the provided class '" + DummyClassNoGoodConstructor.class.getName() + "'.")
                .getCause().isInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void buildClassAbstract() {
        MultipleRegexBuildExecutor<DummyClassAbstract> classUnderTest =
                new MultipleRegexBuildExecutor<>(DummyClassAbstract.class, Arrays.asList(Pattern.compile("NEXTPATTERNMATCHES"), Pattern.compile("^(?<fail>.*)$")));

        assertThatThrownBy(() -> classUnderTest.build("test")).isInstanceOf(BuildException.class)
                .hasMessage("Unable to instantiate the provided class '" + DummyClassAbstract.class.getName() + "' because it is abstract.")
                .getCause().isInstanceOf(InstantiationException.class);
    }

}
