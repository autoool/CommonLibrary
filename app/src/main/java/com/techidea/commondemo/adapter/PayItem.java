package com.techidea.commondemo.adapter;

/**
 * Created by zhangchao on 2016/4/24.
 */
public class PayItem {
    private int id;
    private String name;

    public PayItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
