package net.aulang.lang.oauth.model;

import java.io.Serializable;

public class Server implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String logoUrl;

    public Server() {

    }

    public Server(String id, String name, String logoUrl) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}