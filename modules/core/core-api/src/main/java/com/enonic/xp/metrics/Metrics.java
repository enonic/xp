package com.enonic.xp.metrics;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class Metrics
{
    private Metrics()
    {
    }

    public static Counter counter( final String name, String... tags )
    {
        final io.micrometer.core.instrument.Counter counter = io.micrometer.core.instrument.Metrics.globalRegistry.counter( name, tags );
        return new Counter()
        {
            @Override
            public void increment()
            {
                counter.increment();
            }

            @Override
            public void increment( final double amount )
            {
                counter.increment( amount );
            }
        };
    }

    public static Timer timer( final String name, String... tags )
    {
        final io.micrometer.core.instrument.Timer timer = io.micrometer.core.instrument.Metrics.globalRegistry.timer( name, tags );
        return new Timer()
        {
            @Override
            public void record( long value, TimeUnit unit )
            {
                timer.record( value, unit );
            }

            @Override
            public <T> T record( final Supplier<T> supplier )
            {
                return timer.record( supplier );
            }

            @Override
            public <T> T recordCallable( final Callable<T> callable )
                throws Exception
            {
                return timer.recordCallable( callable );
            }

            @Override
            public void record( final Runnable f )
            {
                timer.record( f );
            }
        };
    }
}
