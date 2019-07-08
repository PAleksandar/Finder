package com.foodfinder.acount;

import java.util.concurrent.atomic.AtomicInteger;

public class UUID {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static int getNextNumber()
    {
        return counter.incrementAndGet();
    }

}
