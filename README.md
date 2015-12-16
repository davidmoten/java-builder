java-builder
============

Generate java builder pattern from a list of variable declarations

Do it online at http://java-builder.herokuapp.com/builder.

Run locally
--------------
```bash
git clone https://github.com/davidmoten/java-builder.git
cd java-builder
mvn jetty:run
```

Then go to [http://localhost:8080](http://localhost:8080).

Example
------------
Converts this java fragment:
```java
public class Fred {

private String name;
private int count;
```
to this:
```java
    private final String name;
    private final int count;

    private Fred(String name, int count){
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private int count;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Fred build() {
            return new Fred(name, count);
        }
    }
```
Known limitations
----------------------

javadoc and annotations might confuse it as would comma separated field declarations.

