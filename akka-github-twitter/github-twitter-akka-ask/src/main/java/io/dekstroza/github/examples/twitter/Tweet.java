package io.dekstroza.github.examples.twitter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tweet {

    private final String text;

    @JsonCreator
    public Tweet(String text) {
        this.text = text;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Tweet tweet = (Tweet) o;

        return getText().equals(tweet.getText());
    }

    @Override
    public int hashCode() {
        return getText().hashCode();
    }

}
