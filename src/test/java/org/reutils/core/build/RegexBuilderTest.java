package org.reutils.core.build;

import org.junit.jupiter.api.Test;
import org.reutils.core.build.builders.MultipleRegexBuilder;
import org.reutils.core.build.builders.RegexBuilder;
import org.reutils.core.build.builders.SingleRegexBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RegexBuilderTest {

    @Test
    public void ofPattern() {
        final Pattern pattern = mock(Pattern.class);
        final SingleRegexBuilder builder = RegexBuilder.of(pattern);

        assertThat(builder.getPattern()).isSameAs(pattern);
    }

    @Test
    public void ofString() {
        final String string = "test";
        final SingleRegexBuilder builder = RegexBuilder.of(string);

        assertThat(builder.getPattern().pattern()).isSameAs(string);
    }

    @Test
    public void ofPatternCollection() {
        final Pattern pattern1 = mock(Pattern.class);
        final Pattern pattern2 = mock(Pattern.class);
        final Pattern pattern3 = mock(Pattern.class);
        final Collection<Pattern> collection = Arrays.asList(pattern1, pattern2, pattern3);

        final MultipleRegexBuilder builder = RegexBuilder.of(collection);

        assertThat(builder.getPatterns()).containsExactly(pattern1, pattern2, pattern3);
    }

    @Test
    public void ofPatterns() {
        final Pattern pattern1 = mock(Pattern.class);
        final Pattern pattern2 = mock(Pattern.class);
        final Pattern pattern3 = mock(Pattern.class);

        final MultipleRegexBuilder builder = RegexBuilder.of(pattern1, pattern2, pattern3);

        assertThat(builder.getPatterns()).containsExactly(pattern1, pattern2, pattern3);
    }

    @Test
    public void ofStrings() {
        final String string1 = "test1";
        final String string2 = "test2";
        final String string3 = "test3";

        final MultipleRegexBuilder builder = RegexBuilder.of(string1, string2, string3);

        assertThat(builder.getPatterns()).hasSize(3);
        assertThat(builder.getPatterns().stream().map(Pattern::pattern)).containsExactly(string1, string2, string3);
    }

}
