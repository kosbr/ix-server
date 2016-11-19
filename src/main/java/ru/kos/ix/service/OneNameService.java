package ru.kos.ix.service;

/**
 * Created by Константин on 09.04.2016.
 */
public class OneNameService {

    public String method(final String a, final String b) {
        return a + b;
    }

    public String method(final String a, final String b, final String c) {
        return a + b + c;
    }

    public String method(final String a, final String b, final Integer c) {
        return a + b + c + " last is int";
    }
}
