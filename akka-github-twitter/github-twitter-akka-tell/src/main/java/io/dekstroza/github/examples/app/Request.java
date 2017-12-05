package io.dekstroza.github.examples.app;

import io.dekstroza.github.examples.common.SearchSummary;

import java.util.List;
import java.util.function.Consumer;

public class Request {

    private final Consumer<List<SearchSummary>> completionFunction;
    private final String searchWord;

    public Request(Consumer<List<SearchSummary>> completionFunction, String searchWord) {
        this.completionFunction = completionFunction;
        this.searchWord = searchWord;
    }

    public Consumer<List<SearchSummary>> getCompletionFunction() {
        return completionFunction;
    }

    public String getSearchWord() {
        return searchWord;
    }
}
