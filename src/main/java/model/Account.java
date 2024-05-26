package model;

import jakarta.xml.bind.annotation.XmlAnyElement;

public class Account {
    @XmlAnyElement
    private int id;
    @XmlAnyElement
    private String username;
    @XmlAnyElement
    private String email;
    @XmlAnyElement
    private String passwordHash;
    @XmlAnyElement
    private String telephone;
    @XmlAnyElement
    private String salt;

    public Account(){}

    public Account(int id, String username, String email, String passwordHash,String telephone,String salt) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPasswordHash(passwordHash);
        setSalt(salt);
        setTelephone(telephone);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
