package com.nanosai.rionops.rion.pojo;

/**
 * Created by jjenkov on 18/03/2017.
 */
public class RecursivePojo {

    private String name = null;

    private RecursivePojo child1 = null;
    private RecursivePojo child2 = null;

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public RecursivePojo getChild1() {
        return child1;
    }

    public void setChild1(RecursivePojo child1) {
        this.child1 = child1;
    }

    public RecursivePojo getChild2() {
        return child2;
    }

    public void setChild2(RecursivePojo child2) {
        this.child2 = child2;
    }
}
