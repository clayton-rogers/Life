package com.gmail.claytonrogers53.life.Util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Keeps a list of recently added items and returns a average of them when prompted. The average is always returned as
 * a double but the list of items is stored as type T. The calculation of the average is also done as a double, so a
 * pool with both very large and very small values could have noticeable error.
 *
 * Created by Clayton on 31/1/2015.
 */
public class RollingAverage<T extends Number> {

    private final Queue<T> items;
    private double average;
    private boolean hasNotBeenModified;
    private final int size;

    /**
     * Creates a rolling average which can hold up to the given size. The size must be greater than 1.
     *
     * @param size
     *        The max size of the rolling average.
     */
    public RollingAverage(int size) {
        if (size <= 1) {
            String message = "Attempted to initialize an empty Rolling average.";
            Log.error(message);
            throw new IllegalArgumentException(message);
        }
        this.size = size;
        items = new LinkedList<>();
    }

    /**
     * Adds a number to the list to be averaged. If the list is at capacity, the oldest one is bumped off.
     *
     * @param number
     *        The number to be added to the average pool.
     */
    public void addToPool(T number) {
        items.add(number);
        if (items.size() > size) {
            items.remove();
        }
        hasNotBeenModified = false;
    }

    /**
     * Queries the current average of the pool.
     *
     * @return The average of the numbers currently in the pool.
     */
    public double getAverage() {
        if (hasNotBeenModified) {
            return average;
        }

        average = 0.0;
        for (T item : items) {
            average += item.doubleValue();
        }
        average /= (double)items.size();
        hasNotBeenModified = true;

        return average;
    }
}
