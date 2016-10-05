package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import java.io.Serializable;
import java.util.Date;

public class Search implements Serializable {

    private String  keyword, id, name, type;

    public Search() {

    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
