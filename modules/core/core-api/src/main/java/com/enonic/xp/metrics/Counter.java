package com.enonic.xp.metrics;

public interface Counter
{
    void increment();

    void increment( double amount );
}
