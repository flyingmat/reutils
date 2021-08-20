package org.reutils.core.build;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reutils.core.build.dummies.*;
import org.reutils.core.build.exceptions.BuildException;
import org.reutils.core.build.executors.SingleRegexBuildExecutor;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SingleRegexBuildExecutorTest {

    @ParameterizedTest
    @MethodSource(value = "_buildSuccess")
    public <C> void buildSuccess(final String input, final Pattern pattern, final Class<C> clazz) {
        SingleRegexBuildExecutor<C> classUnderTest = new SingleRegexBuildExecutor<>(clazz, pattern);

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
                        input,
                        Pattern.compile("^(?<value1>.*) is the first value, then (?<value2>.*), (?<value3>.*) and (?<value4>.*)$"),
                        DummyClass.class),
                Arguments.of(
                        input,
                        Pattern.compile("^(?<v1>.*) is the first value, then (?<test>.*), (?<value3>.*) and (?<value4>.*)$"),
                        DummyClassGroupsSpecified.class),
                Arguments.of(
                        input,
                        Pattern.compile("^(?<v1>.*) is the first value, then (?<test>.*), (?<value3>.*) and (?<value4>.*)$"),
                        DummyClassGroupsNumbered.class)
        );
    }

    @Test
    public void buildDoesNotMatch() {
        SingleRegexBuildExecutor<DummyClass> classUnderTest =
                new SingleRegexBuildExecutor<>(DummyClass.class, Pattern.compile("^$"));

        assertThatThrownBy(() -> classUnderTest.build("fail")).isInstanceOf(BuildException.class)
                .hasMessage("The provided pattern does not match against the input.");
    }

    @Test
    public void buildGroupNotFound() {
        SingleRegexBuildExecutor<DummyClass> classUnderTest =
                new SingleRegexBuildExecutor<>(DummyClass.class, Pattern.compile("^(?<fail>.*)$"));

        assertThatThrownBy(() -> classUnderTest.build("test")).isInstanceOf(BuildException.class)
                .hasMessageMatching("^Unable to set field 'value\\d'\\.$")
                .getCause().isInstanceOf(IllegalArgumentException.class).hasMessageMatching("^No group with name <value\\d>$");
    }

    @Test
    public void buildNoArgumentsConstructorNotFound() {
        SingleRegexBuildExecutor<DummyClassNoGoodConstructor> classUnderTest =
                new SingleRegexBuildExecutor<>(DummyClassNoGoodConstructor.class, Pattern.compile("^(?<value1>.*)$"));

        assertThatThrownBy(() -> classUnderTest.build("test")).isInstanceOf(BuildException.class)
                .hasMessage("Unable to find a constructor taking no arguments in the provided class '" + DummyClassNoGoodConstructor.class.getName() + "'.")
                .getCause().isInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void buildClassAbstract() {
        SingleRegexBuildExecutor<DummyClassAbstract> classUnderTest =
                new SingleRegexBuildExecutor<>(DummyClassAbstract.class, Pattern.compile("^(?<value1>.*)$"));

        assertThatThrownBy(() -> classUnderTest.build("test")).isInstanceOf(BuildException.class)
                .hasMessage("Unable to instantiate the provided class '" + DummyClassAbstract.class.getName() + "' because it is abstract.")
                .getCause().isInstanceOf(InstantiationException.class);
    }

}
