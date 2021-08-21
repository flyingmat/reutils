import org.reutils.annotations.Group;
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

    }

}
