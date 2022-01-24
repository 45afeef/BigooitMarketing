package com.bigooit.marketing.DataModels;

enum PoolType{InstagramBusinessAccount,FacebookPage}

public class ContentPool {
    long id;
    String name;
    PoolType type;

    public ContentPool(){}

    public ContentPool(long id, String name, PoolType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
