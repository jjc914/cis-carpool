package com.example.cis_carpool.abstraction;

/**
 * A class that is similar to Runnable, but also takes in an array of object arguments.
 */
public interface ParameterizedRunnable {
    /**
     * Run the code.
     * @param args runnable arguments.
     */
    public void run(Object... args);
}
