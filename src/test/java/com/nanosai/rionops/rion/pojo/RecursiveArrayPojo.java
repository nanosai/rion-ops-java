package com.nanosai.rionops.rion.pojo;

/**
 * Created by jjenkov on 18/03/2017.
 */
public class RecursiveArrayPojo {

    private String name = null;

    private RecursiveArrayPojo[] children = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecursiveArrayPojo[] getChildren() {
        return children;
    }

    public void setChildren(RecursiveArrayPojo[] children) {
        this.children = children;
    }
}
