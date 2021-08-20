import org.reutils.annotations.Group;
import org.reutils.core.build.exceptions.BuildException;
import org.reutils.core.build.ReBuilder;

import java.util.regex.Pattern;

public class Main {

    public static class Dog {

        @Group
        private String name;

        @Group(group = "lol")
        private String age;

    }

    public static void main(String[] args) throws BuildException {
        Dog dog = ReBuilder.of(Dog.class, Pattern.compile("(?<name>.*)\\s+is\\s+a\\s+(?<lol>\\d+)\\s+year\\s+old\\s+dog"))
                           .build("Mr. Doge is a 4 year old dog");

        System.out.printf("Dog\nName: %s\nAge: %s", dog.name, dog.age);
    }

}
