package com.enonic.xp.metrics;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface Timer
{
    void record( long value, TimeUnit unit );

    <T> T record( Supplier<T> supplier );

    <T> T recordCallable( Callable<T> callable )
        throws Exception;

    void record( Runnable f );
}

