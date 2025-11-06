package com.cloume.ecnu.authserver.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonGsonBuilder {

    public static final GsonBuilder INSTANCE = new GsonBuilder();

    public static Gson create() {
        return INSTANCE.create();
    }
}
