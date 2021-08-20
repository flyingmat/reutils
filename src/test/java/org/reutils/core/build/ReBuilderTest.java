package org.reutils.core.build;

import org.junit.jupiter.api.Test;
import org.reutils.core.build.dummies.DummyClass;
import org.reutils.core.build.executors.MultipleRegexBuildExecutor;
import org.reutils.core.build.executors.SingleRegexBuildExecutor;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ReBuilderTest {

    @Test
    public void ofPattern() {
        final Pattern mockPattern = mock(Pattern.class);

        final SingleRegexBuildExecutor<DummyClass> buildExecutor = ReBuilder.of(DummyClass.class, mockPattern);

        assertThat(buildExecutor).hasFieldOrPropertyWithValue("pattern", mockPattern);
    }

    @Test
    public void ofString() {
        final String string = "";

        final SingleRegexBuildExecutor<DummyClass> buildExecutor = ReBuilder.of(DummyClass.class, string);

        assertThat(buildExecutor.getPattern().pattern()).isSameAs(string);
    }

    @Test
    public void ofPatternCollection() {
        final Pattern mockPattern1 = mock(Pattern.class), mockPattern2 = mock(Pattern.class), mockPattern3 = mock(Pattern.class);
        final Collection<Pattern> patterns = Arrays.asList(mockPattern1, mockPattern2, mockPattern3);

        final MultipleRegexBuildExecutor<DummyClass> buildExecutor = ReBuilder.of(DummyClass.class, patterns);

        assertThat(buildExecutor.getPatterns()).isSameAs(patterns);
    }

    @Test
    public void ofPatterns() {
        final Pattern mockPattern1 = mock(Pattern.class), mockPattern2 = mock(Pattern.class), mockPattern3 = mock(Pattern.class);

        final MultipleRegexBuildExecutor<DummyClass> buildExecutor = ReBuilder.of(DummyClass.class, mockPattern1, mockPattern2, mockPattern3);

        assertThat(buildExecutor.getPatterns()).containsExactly(mockPattern1, mockPattern2, mockPattern3);
    }

    @Test
    public void ofStrings() {
        final String string1 = "1", string2 = "2", string3 = "3";

        final MultipleRegexBuildExecutor<DummyClass> buildExecutor = ReBuilder.of(DummyClass.class, string1, string2, string3);

        assertThat(buildExecutor.getPatterns().stream().map(Pattern::pattern)).containsExactly(string1, string2, string3);
    }
}
