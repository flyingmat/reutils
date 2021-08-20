# reutils
Regex utilities for Java

## Examples

### Building an object from Regex capture groups

```java
import org.reutils.annotations.Group;
import org.reutils.core.build.ReBuilder;

public class Jdk {

    // Match group "version" (same as field name)
    @Group
    public String version;

    // Match group number 2
    @Group(group = "$2")
    public String vendor;

    // Match group "runtime"
    @Group(group = "runtime")
    public String runtimePath;

    public static void main(String[] args) {
        final String input = "Java version: 16.0.2, vendor: Oracle Corporation, runtime: ~/.jdks/jdk-16.0.2";
        final Jdk jdk = ReBuilder.of(Jdk.class, "^Java version: (?<version>.*), vendor: (?<vendor>.*), runtime: (?<runtime>.*)$").build(input);
    }

}
```