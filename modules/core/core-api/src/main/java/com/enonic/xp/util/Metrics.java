package com.enonic.xp.util;

import java.util.function.Supplier;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;

public final class Metrics
{
    private static final Metrics INSTANCE = new Metrics();

    private final MetricRegistry registry;

    private Metrics()
    {
        this.registry = SharedMetricRegistries.getOrCreate( "xp" );
    }

    public static Meter meter( final String name )
    {
        return registry().meter( name );
    }

    public static Meter meter( final Class<?> clz, final String name )
    {
        return meter( MetricRegistry.name( clz, name ) );
    }

    public static Counter counter( final String name )
    {
        return registry().counter( name );
    }

    public static Counter counter( final Class<?> clz, final String name )
    {
        return counter( MetricRegistry.name( clz, name ) );
    }

    public static Histogram histogram( final String name )
    {
        return registry().histogram( name );
    }

    public static Histogram histogram( final Class<?> clz, final String name )
    {
        return histogram( MetricRegistry.name( clz, name ) );
    }

    public static Timer timer( final String name )
    {
        return registry().timer( name );
    }

    public static Timer timer( final Class<?> clz, final String name )
    {
        return timer( MetricRegistry.name( clz, name ) );
    }

    public static <T extends Metric> T register( final String name, final T metric )
    {
        return registry().register( name, metric );
    }

    public static <T extends Metric> T register( final Class<?> clz, final String name, final T metric )
    {
        return register( MetricRegistry.name( clz, name ), metric );
    }

    public static void registerAll( final MetricSet set )
    {
        registry().registerAll( set );
    }

    public static <T> T time( final Timer timer, final Supplier<T> supplier )
    {
        try (Timer.Context ignored = timer.time())
        {
            return supplier.get();
        }
    }

    public static void removeAll( final Class<?> clz )
    {
        registry().removeMatching( ( name, metric ) -> name.startsWith( clz.getName() + "." ) );
    }

    public static MetricRegistry registry()
    {
        return INSTANCE.registry;
    }
}
