import org.reutils.annotations.Group;
import org.reutils.core.build.builders.MultipleRegexBuilder;
import org.reutils.core.build.builders.RegexBuilder;
import org.reutils.core.build.builders.SingleRegexBuilder;
import org.reutils.core.build.exceptions.BuildException;

import java.util.regex.Pattern;

public class Main {

    public static class Dog {

        @Group
        private String name;

        @Group("lol")
        private String age;

    }

    public static void main(String[] args) throws BuildException {
        RegexBuilder.of(Pattern.compile("")).build("", Dog.class);
    }

}
