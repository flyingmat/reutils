# reutils
Regex utilities for Java

## Examples

### Building an object from Regex capture groups

```java
import org.reutils.annotations.Group;
import org.reutils.core.build.builders.RegexBuilder;

import java.util.Optional;
import java.util.regex.Pattern;

public class Jdk {

        public static class Version {

            // Use "toInt" for all fields (specified by builder)

            @Group(function = "toInt")
            public Integer major;

            @Group(function = "toInt")
            public Integer minor;

            @Group(function = "toInt")
            public Integer patch;

            @Override
            public String toString() {
                return String.format("%d.%d.%d", major, minor, patch);
            }

            public static Version of(final String version) {
                // Specify "toInt" as Integer.parseInt(String)
                return RegexBuilder.of("(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)").with("toInt", Integer::parseInt)
                        .build(version, Version.class).orElse(null);
            }

        }

        // Match group "version" (same as field name)
        @Group
        public Version version;

        // Match group "vendor" (same as field name)
        @Group
        public String vendor;

        // Match group "runtime"
        @Group(value = "runtime")
        public String runtimePath;

        public static void main(String[] args) {
            final String input = "Java version: 16.0.2, vendor: Oracle Corporation, runtime: ~/.jdks/jdk-16.0.2";
            final Pattern pattern = Pattern.compile("^Java version: (?<version>.*), vendor: (?<vendor>.*), runtime: (?<runtime>.*)$");

            // Map group "version" using Version.of(String)
            final Optional<Jdk> jdk = RegexBuilder.of(pattern).map("version", Version::of).build(input, Jdk.class);

            jdk.ifPresent(value -> System.out.printf("%s JDK %s, %s", value.vendor, value.version, value.runtimePath));
            // Prints "Oracle Corporation JDK 16.0.2, ~/.jdks/jdk-16.0.2"
        }

    }
    
}
```
