package com.enonic.xp.perftest.content;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MICROSECONDS )
@Warmup( iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS )
@Measurement( iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS )
@Fork( 1 )
@State( Scope.Benchmark )
public class ContentCreateBenchmark
{
    private Bootstrap bs;

    private final AtomicInteger seq = new AtomicInteger();

    @Setup( Level.Trial )
    public void setUp()
        throws Exception
    {
        bs = new Bootstrap();
        bs.start();
    }

    @TearDown( Level.Trial )
    public void tearDown()
    {
        bs.stop();
    }

    @Benchmark
    public Content create()
    {
        return bs.contentService.create( CreateContentParams.create()
            .name( "perf-" + seq.getAndIncrement() )
            .displayName( "perf" )
            .parent( ContentPath.ROOT )
            .contentData( new PropertyTree() )
            .type( ContentTypeName.folder() )
            .build() );
    }
}
