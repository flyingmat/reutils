package org.reutils.core.build;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reutils.core.build.builders.MultipleRegexBuilder;
import org.reutils.core.build.dummies.*;
import org.reutils.core.build.exceptions.BuildException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MultipleRegexBuilderTest {

    @ParameterizedTest
    @MethodSource(value = "_buildSuccess")
    public <T> void buildSuccess(final String input, final Class<T> clazz, final Collection<Pattern> patterns) {
        final Optional<T> result = new MultipleRegexBuilder(patterns).build(input, clazz);

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
                        Arrays.asList(
                                Pattern.compile("^THE FIRST value is: (?<value1>.*); THE SECOND value is: (?<value2>.*); THE THIRD value is: (?<value3>.*); THE SECOND value is: (?<value4>.*)$"),
                                Pattern.compile("^(?<value1>.*) is the first value, then (?<value2>.*), (?<value3>.*) and (?<value4>.*)$"))),
                Arguments.of(
                        input,
                        DummyClassGroupsSpecified.class,
                        Arrays.asList(
                                Pattern.compile("^DOESNOTMATCH$"),
                                Pattern.compile("I DONT MATCH (?<v1>EITHER)"),
                                Pattern.compile("^(?<v1>.*) is the first value, then (?<test>.*), (?<value3>.*) and (?<value4>.*)$"),
                                Pattern.compile("^$")))
        );
    }

    @Test
    public void buildNoneMatch() {
        final Pattern pattern1 = Pattern.compile("^a$");
        final Pattern pattern2 = Pattern.compile("^b$");

        final Optional<DummyClass> result = new MultipleRegexBuilder(Arrays.asList(pattern1, pattern2)).build("c", DummyClass.class);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void buildGroupNotFound() {
        final Pattern pattern1 = Pattern.compile("^a$");
        final Pattern pattern2 = Pattern.compile("^b$");

        final MultipleRegexBuilder builder = new MultipleRegexBuilder(Arrays.asList(pattern1, pattern2));

        assertThatThrownBy(() -> builder.build("b", DummyClass.class)).isInstanceOf(BuildException.class)
                .hasMessageMatching("^Unable to set field 'value\\d'\\.$")
                .getCause().isInstanceOf(IllegalArgumentException.class).hasMessageMatching("^No group with name <value\\d>$");
    }

    @Test
    public void buildRequiredGroupIsNull() {
        final MultipleRegexBuilder builder = new MultipleRegexBuilder(Collections.singleton(Pattern.compile("^(?<value1>v1)?(?: (?<value2>v2))?$")));

        assertThatThrownBy(() -> builder.build("", DummyClassOptionalGroup.class)).isInstanceOf(BuildException.class)
                .hasMessage("Group 'value1' did not match any input for non-optional field 'value1'.");
    }

    @Test
    public void buildNoArgumentsConstructorNotFound() {
        final MultipleRegexBuilder builder = new MultipleRegexBuilder(Collections.singleton(Pattern.compile("test")));

        assertThatThrownBy(() -> builder.build("test", DummyClassNoGoodConstructor.class)).isInstanceOf(BuildException.class)
                .hasMessage("Unable to find a constructor taking no arguments in the provided class '" + DummyClassNoGoodConstructor.class.getName() + "'.")
                .getCause().isInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void buildClassAbstract() {
        final MultipleRegexBuilder builder = new MultipleRegexBuilder(Collections.singleton(Pattern.compile("test")));

        assertThatThrownBy(() -> builder.build("test", DummyClassAbstract.class)).isInstanceOf(BuildException.class)
                .hasMessage("Unable to instantiate the provided class '" + DummyClassAbstract.class.getName() + "' because it is abstract.")
                .getCause().isInstanceOf(InstantiationException.class);
    }

    @ParameterizedTest
    @MethodSource("_buildMapSuccess")
    public <T, O> void buildMapSuccess(final String input, final Class<T> clazz, final Function<String,O> mapping, final O expected, final Collection<Pattern> patterns) {
        final Optional<T> result = new MultipleRegexBuilder(patterns).map("mapthis", mapping).build(input, clazz);

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
                        Arrays.asList(
                                Pattern.compile("^$"),
                                Pattern.compile("^not a (?<string>.+): (?<mapthis>\\d+)$")
                        )
                ),
                Arguments.of(
                        "not a string: 2021-08-21",
                        DummyClassMapLocalDate.class,
                        (Function<String, LocalDate>) LocalDate::parse,
                        LocalDate.of(2021, 8, 21),
                        Arrays.asList(
                                Pattern.compile("^$"),
                                Pattern.compile("^not a (?<string>.+): (?<mapthis>\\d{4}-\\d{2}-\\d{2})$")
                        )
                )
        );
    }
}
