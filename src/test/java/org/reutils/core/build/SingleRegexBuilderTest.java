package org.reutils.core.build;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
    public <T> void buildSuccess(final String input, final Pattern pattern, final Class<T> clazz) {
        final T result = SingleRegexBuilder.of(pattern).build(input).as(clazz);

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
                        DummyClass.class
                ),
                Arguments.of(
                        input,
                        Pattern.compile("^(?<v1>.*) is the first value, then (?<test>.*), (?<value3>.*) and (?<value4>.*)$"),
                        DummyClassGroupsSpecified.class
                )
        );
    }

    @Test
    public void buildDoesNotMatch() {
        final SingleRegexBuilder builder = SingleRegexBuilder.of(Pattern.compile("^$")).build("fail");
        
        assertThatThrownBy(() -> builder.as(DummyClass.class)).isInstanceOf(BuildException.class)
                .hasMessage("The provided pattern does not match against the input.");
    }

    @Test
    public void buildGroupNotFound() {
        final SingleRegexBuilder builder = SingleRegexBuilder.of(Pattern.compile("^(?<fail>.*)$")).build("test");

        assertThatThrownBy(() -> builder.as(DummyClass.class)).isInstanceOf(BuildException.class)
                .hasMessageMatching("^Unable to set field 'value\\d'\\.$")
                .getCause().isInstanceOf(IllegalArgumentException.class).hasMessageMatching("^No group with name <value\\d>$");
    }

    @Test
    public void buildNoArgumentsConstructorNotFound() {
        final SingleRegexBuilder builder = SingleRegexBuilder.of(Pattern.compile("^(?<value1>.*)$")).build("test");

        assertThatThrownBy(() -> builder.as(DummyClassNoGoodConstructor.class)).isInstanceOf(BuildException.class)
                .hasMessage("Unable to find a constructor taking no arguments in the provided class '" + DummyClassNoGoodConstructor.class.getName() + "'.")
                .getCause().isInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void buildClassAbstract() {
        final SingleRegexBuilder builder = SingleRegexBuilder.of(Pattern.compile("^(?<value1>.*)$")).build("test");

        assertThatThrownBy(() -> builder.as(DummyClassAbstract.class)).isInstanceOf(BuildException.class)
                .hasMessage("Unable to instantiate the provided class '" + DummyClassAbstract.class.getName() + "' because it is abstract.")
                .getCause().isInstanceOf(InstantiationException.class);
    }

    @Test
    public void buildValidOptional() {
        final String input = "v1 is the first value, then v2, v3 and v4";
        final Pattern pattern = Pattern.compile("^(?<value1>.*) is the first value, then (?<value2>.*), (?<value3>.*) and (?<value4>.*)$");

        final Optional<DummyClass> result = SingleRegexBuilder.of(pattern).build(input).asOptional(DummyClass.class);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void buildEmptyOptional() {
        final String input = "";
        final Pattern pattern = Pattern.compile("^(?<value1>.*) is the first value, then (?<value2>.*), (?<value3>.*) and (?<value4>.*)$");

        final Optional<DummyClass> result = SingleRegexBuilder.of(pattern).build(input).asOptional(DummyClass.class);

        assertThat(result.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("_buildMapSuccess")
    public <T, O> void buildMapSuccess(final String input, final Pattern pattern, final Class<T> clazz, final Function<String,O> mapping, final O expected) {
        final T result = SingleRegexBuilder.of(pattern).map("mapthis", mapping).build(input).as(clazz);

        assertThat(result).hasFieldOrPropertyWithValue("string", "string");
        assertThat(result).hasFieldOrPropertyWithValue("mapthis", expected);
    }

    private static Stream<Arguments> _buildMapSuccess() {
        return Stream.of(
                Arguments.of(
                        "not a string: 123",
                        Pattern.compile("^not a (?<string>.+): (?<mapthis>\\d+)$"),
                        DummyClassMapInteger.class,
                        (Function<String,Integer>) Integer::parseInt,
                        123
                ),
                Arguments.of(
                        "not a string: 2021-08-21",
                        Pattern.compile("^not a (?<string>.+): (?<mapthis>\\d{4}-\\d{2}-\\d{2})$"),
                        DummyClassMapLocalDate.class,
                        (Function<String,LocalDate>) LocalDate::parse,
                        LocalDate.of(2021, 8, 21)
                )
        );
    }

}
