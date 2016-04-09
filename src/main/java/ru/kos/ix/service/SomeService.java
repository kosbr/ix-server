package ru.kos.ix.service;

/**
 * Created by Константин on 08.04.2016.
 */
public class SomeService {

    public String toUpper(String str) {
        return str.toUpperCase();
    }

    public Integer plus(Integer a, Integer b) {
        return a + b;
    }

    public void sleep(Integer ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {
        }
    }

    public void error() {
        throw new NullPointerException();
    }

}
