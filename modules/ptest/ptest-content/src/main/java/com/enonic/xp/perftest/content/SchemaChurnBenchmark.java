package com.enonic.xp.perftest.content;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.BenchmarkParams;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.GetContentTypeParams;

/**
 * Reads of content types while the schema of the application keeps changing: four threads read content types including form
 * fragments, one thread rewrites a fragment every {@code writeIntervalMs}. Every write moves the generation of the whole
 * application, so the more frequent the writes, the more reads miss the cache. The score of {@code read} is the one to compare.
 */
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MICROSECONDS )
@Warmup( iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS )
@Measurement( iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS )
@Fork( 1 )
@State( Scope.Group )
public class SchemaChurnBenchmark
{
    private static final int CONTENT_TYPES = 500;

    @Param( {"10", "100", "1000"} )
    public int writeIntervalMs;

    private final SchemaFixture fixture = new SchemaFixture();

    private final LongAdder reads = new LongAdder();

    private int nextFragment;

    @Setup( Level.Trial )
    public void setUp()
        throws Exception
    {
        fixture.start( CONTENT_TYPES );
    }

    @Benchmark
    @Group( "churn" )
    @GroupThreads( 4 )
    public ContentType read()
    {
        reads.increment();
        return fixture.contentTypeService.getByName( GetContentTypeParams.from(
            fixture.typesWithFragments.get( ThreadLocalRandom.current().nextInt( fixture.typesWithFragments.size() ) ) ) );
    }

    @Benchmark
    @Group( "churn" )
    @GroupThreads( 1 )
    public void write()
        throws InterruptedException
    {
        Thread.sleep( writeIntervalMs );
        fixture.writeFragment( nextFragment );
        nextFragment = ( nextFragment + 1 ) % SchemaFixture.FRAGMENTS;
    }

    @TearDown( Level.Trial )
    public void tearDown( final BenchmarkParams params )
        throws Exception
    {
        fixture.printNodeReads( params.getBenchmark(), "contentTypes=" + CONTENT_TYPES + " writeIntervalMs=" + writeIntervalMs,
                                reads.sum() );
        fixture.stop();
    }
}
