package com.enonic.xp.launcher.impl.util;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Periodically logs a single line so that an otherwise quiet node can be told apart from a broken log pipeline.
 * <p>
 * Runs in the launcher and depends on nothing but the JVM, so it keeps beating whatever state the OSGi framework is in.
 */
public final class Heartbeat
{
    private static final Logger LOG = LoggerFactory.getLogger( "com.enonic.xp.heartbeat" );

    private static final long MB = 1024 * 1024;

    private final String version;

    private final AtomicLong seq = new AtomicLong();

    private volatile ScheduledExecutorService executor;

    public Heartbeat( final String version )
    {
        this.version = version;
    }

    public void start( final Duration interval )
    {
        this.executor = Executors.newSingleThreadScheduledExecutor( r -> {
            final Thread thread = new Thread( r, "Heartbeat" );
            thread.setDaemon( true );
            return thread;
        } );
        this.executor.scheduleAtFixedRate( this::beat, interval.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS );
    }

    public void stop()
    {
        if ( this.executor != null )
        {
            this.executor.shutdownNow();
        }
    }

    private void beat()
    {
        try
        {
            LOG.info( buildMessage() );
        }
        catch ( Throwable t )
        {
            // never let anything thrown cancel future heartbeats
        }
    }

    String buildMessage()
    {
        return "XP Heartbeat seq=" + seq.incrementAndGet() + " uptime=" + safe( Heartbeat::uptime ) + " version=" + version +
            " heap=" + safe( Heartbeat::heap ) + " gc=" + safe( Heartbeat::gc ) + " threads=" +
            safe( () -> ManagementFactory.getThreadMXBean().getThreadCount() );
    }

    private static String uptime()
    {
        final Duration uptime = Duration.ofMillis( ManagementFactory.getRuntimeMXBean().getUptime() );
        return uptime.toDays() + "d" + uptime.toHoursPart() + "h" + uptime.toMinutesPart() + "m" + uptime.toSecondsPart() + "s";
    }

    private static String heap()
    {
        final MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return heap.getUsed() / MB + "/" + heap.getMax() / MB + "MB";
    }

    private static String gc()
    {
        long count = 0;
        long time = 0;
        for ( final GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans() )
        {
            count += Math.max( bean.getCollectionCount(), 0 );
            time += Math.max( bean.getCollectionTime(), 0 );
        }
        return count + "/" + time + "ms";
    }

    private static String safe( final Supplier<Object> supplier )
    {
        try
        {
            return String.valueOf( supplier.get() );
        }
        catch ( Throwable t )
        {
            return "n/a";
        }
    }
}
