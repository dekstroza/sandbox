package io.dekstroza.github.examples.common;

public class SearchRequest {

    private final String searchTerm;

    public SearchRequest(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SearchRequest that = (SearchRequest) o;

        return getSearchTerm().equals(that.getSearchTerm());
    }

    @Override
    public int hashCode() {
        return getSearchTerm().hashCode();
    }
}
