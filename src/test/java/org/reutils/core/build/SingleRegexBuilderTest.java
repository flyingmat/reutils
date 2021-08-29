package org.reutils.core.build;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reutils.core.build.builders.RegexBuilder;
import org.reutils.core.build.builders.SingleRegexBuilder;
import org.reutils.core.build.dummies.*;
import org.reutils.core.build.exceptions.BuildException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SingleRegexBuilderTest {

    @ParameterizedTest
    @MethodSource("_buildSuccess")
    public <T> void buildSuccess(final String input, final Class<T> clazz, final Pattern pattern) {
        final Optional<T> result = new SingleRegexBuilder(pattern).build(input, clazz);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).hasFieldOrPropertyWithValue("value1", "v1");
        assertThat(result.get()).hasFieldOrPropertyWithValue("value2", "v2");
        assertThat(result.get()).hasFieldOrPropertyWithValue("value3", "v3");
        assertThat(result.get()).hasFieldOrPropertyWithValue("value4", "v4");
    }

    private static Stream<Arguments> _buildSuccess() {
        final String input = "v1 is the first value, then v2, v3 and v4";
        return Stream.of(
                Arguments.of(
                        input,
                        DummyClass.class,
                        Pattern.compile("^(?<value1>.*) is the first value, then (?<value2>.*), (?<value3>.*) and (?<value4>.*)$")
                ),
                Arguments.of(
                        input,
                        DummyClassGroupsSpecified.class,
                        Pattern.compile("^(?<v1>.*) is the first value, then (?<test>.*), (?<value3>.*) and (?<value4>.*)$")
                )
        );
    }

    @Test
    public void buildDoesNotMatch() {
        final Optional<DummyClass> result = new SingleRegexBuilder(Pattern.compile("^$")).build("fail", DummyClass.class);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void buildGroupNotFound() {
        final SingleRegexBuilder builder = new SingleRegexBuilder(Pattern.compile("^(?<fail>.*)$"));

        assertThatThrownBy(() -> builder.build("test", DummyClass.class)).isInstanceOf(BuildException.class)
                .hasMessageMatching("^Unable to set field 'value\\d'\\.$")
                .getCause().isInstanceOf(IllegalArgumentException.class).hasMessageMatching("^No group with name <value\\d>$");
    }

    @Test
    public void buildOptionalGroupIsNull() {
        final Optional<DummyClassOptionalGroup> result = new SingleRegexBuilder(Pattern.compile("^(?<value1>v1)(?: (?<value2>v2))?$")).build("v1", DummyClassOptionalGroup.class);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).hasFieldOrPropertyWithValue("value1", "v1");
        assertThat(result.get()).hasFieldOrPropertyWithValue("value2", null);

    }

    @Test
    public void buildRequiredGroupIsNull() {
        final SingleRegexBuilder builder = new SingleRegexBuilder(Pattern.compile("^(?<value1>v1)?(?: (?<value2>v2))?$"));

        assertThatThrownBy(() -> builder.build("", DummyClassOptionalGroup.class)).isInstanceOf(BuildException.class)
                .hasMessage("Group 'value1' did not match any input for non-optional field 'value1'.");
    }

    @Test
    public void buildNoArgumentsConstructorNotFound() {
        final SingleRegexBuilder builder = new SingleRegexBuilder(Pattern.compile("^(?<value1>.*)$"));

        assertThatThrownBy(() -> builder.build("test", DummyClassNoGoodConstructor.class)).isInstanceOf(BuildException.class)
                .hasMessage("Unable to find a constructor taking no arguments in the provided class '" + DummyClassNoGoodConstructor.class.getName() + "'.")
                .getCause().isInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void buildClassAbstract() {
        final SingleRegexBuilder builder = new SingleRegexBuilder(Pattern.compile("^(?<value1>.*)$"));

        assertThatThrownBy(() -> builder.build("test", DummyClassAbstract.class)).isInstanceOf(BuildException.class)
                .hasMessage("Unable to instantiate the provided class '" + DummyClassAbstract.class.getName() + "' because it is abstract.")
                .getCause().isInstanceOf(InstantiationException.class);
    }

    @ParameterizedTest
    @MethodSource("_buildMapSuccess")
    public <T, O> void buildMapSuccess(final String input, final Class<T> clazz, final Function<String,O> mapping, final O expected, final Pattern pattern) {
        final Optional<T> result = new SingleRegexBuilder(pattern).map("mapthis", mapping).build(input, clazz);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).hasFieldOrPropertyWithValue("string", "string");
        assertThat(result.get()).hasFieldOrPropertyWithValue("mapthis", expected);
    }

    private static Stream<Arguments> _buildMapSuccess() {
        return Stream.of(
                Arguments.of(
                        "not a string: 123",
                        DummyClassMapInteger.class,
                        (Function<String,Integer>) Integer::parseInt,
                        123,
                        Pattern.compile("^not a (?<string>.+): (?<mapthis>\\d+)$")
                ),
                Arguments.of(
                        "not a string: 2021-08-21",
                        DummyClassMapLocalDate.class,
                        (Function<String,LocalDate>) LocalDate::parse,
                        LocalDate.of(2021, 8, 21),
                        Pattern.compile("^not a (?<string>.+): (?<mapthis>\\d{4}-\\d{2}-\\d{2})$")
                )
        );
    }

    @Test
    public void buildFunctionSuccess() {
        final String input = "not a string: 123";
        final Pattern pattern = Pattern.compile("^not a (?<string>.+): (?<mapthis>\\d+)$");

        final Optional<DummyClassFunctionInteger> result = new SingleRegexBuilder(pattern).with("toInt", Integer::parseInt)
                .build(input, DummyClassFunctionInteger.class);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).hasFieldOrPropertyWithValue("string", "string");
        assertThat(result.get()).hasFieldOrPropertyWithValue("mapthis", 123);
    }

}