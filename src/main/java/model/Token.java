package model;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class Token {
    @XmlAnyElement
    private int tokenId;
    @XmlAnyElement
    private int accountId;
    @XmlAnyElement
    private Timestamp expiration;
    @XmlAnyElement
    private Timestamp createDate;
    @XmlAnyElement
    private Timestamp updatedDate;
    @XmlAnyElement
    private String token;

    public Token() {}

    public Token(int tokenId, int accountId, Timestamp expiration, Timestamp createDate, Timestamp updatedDate, String token) {
        setTokenId(tokenId);
        setAccountId(accountId);
        setExpiration(expiration);
        setCreateDate(createDate);
        setUpdatedDate(updatedDate);
        setToken(token);

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public Timestamp getExpiration() {
        return expiration;
    }

    public void setExpiration(Timestamp expiration) {
        this.expiration = expiration;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }
}
