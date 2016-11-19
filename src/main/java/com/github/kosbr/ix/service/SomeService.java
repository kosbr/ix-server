package com.github.kosbr.ix.service;

/**
 * Created by kosbr on 08.04.2016.
 */
public class SomeService {

    public String toUpper(final String str) {
        return str.toUpperCase();
    }

    public Integer plus(final Integer a, final Integer b) {
        return a + b;
    }

    public void sleep(final Integer ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {
        }
    }

    public void error() {
        throw new NullPointerException("NPE");
    }

}
