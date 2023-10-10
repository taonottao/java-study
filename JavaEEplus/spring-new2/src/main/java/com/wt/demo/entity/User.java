package com.wt.demo.entity;

import org.springframework.objenesis.instantiator.perc.PercInstantiator;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/8 15:39
 */
public class User {

    private int id;
    private String name;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
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
