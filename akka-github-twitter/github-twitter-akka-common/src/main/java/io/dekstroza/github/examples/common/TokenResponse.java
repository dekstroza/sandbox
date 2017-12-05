package io.dekstroza.github.examples.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "token_type", "access_token" })
public class TokenResponse {
    private String tokenType;
    private String accessToken;

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TokenResponse that = (TokenResponse) o;

        if (!getTokenType().equals(that.getTokenType()))
            return false;
        return getAccessToken().equals(that.getAccessToken());
    }

    @Override
    public int hashCode() {
        int result = getTokenType().hashCode();
        result = 31 * result + getAccessToken().hashCode();
        return result;
    }
}
