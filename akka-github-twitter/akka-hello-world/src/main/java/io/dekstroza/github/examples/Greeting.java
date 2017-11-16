package io.dekstroza.github.examples;

public class Greeting {
    private final String message;

    public Greeting(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Greeting greeting = (Greeting) o;

        return getMessage().equals(greeting.getMessage());
    }

    @Override
    public int hashCode() {
        return getMessage().hashCode();
    }
}
