package model;

import jakarta.xml.bind.annotation.XmlAnyElement;

import java.sql.Timestamp;

public class ResetToken {
    @XmlAnyElement
    private int tokenId;
    @XmlAnyElement
    private int accountId;
    @XmlAnyElement
    private String token;
    @XmlAnyElement
    private Timestamp expiration;
    public ResetToken() {}

    public ResetToken(int tokenId, int accountId, String token, Timestamp expiration) {
        this.tokenId = tokenId;
        this.accountId = accountId;
        this.token = token;
        this.expiration = expiration;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getExpiration() {
        return expiration;
    }

    public void setExpiration(Timestamp expiration) {
        this.expiration = expiration;
    }
}
