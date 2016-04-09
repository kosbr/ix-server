package ru.kos.ix.service;

/**
 * Created by Константин on 09.04.2016.
 */
public class OneNameService {

    public String method(String a, String b) {
        return a + b;
    }

    public String method(String a, String b, String c) {
        return a + b + c;
    }

    public String method(String a, String b, Integer c) {
        return a + b + c + " last is int";
    }
}
