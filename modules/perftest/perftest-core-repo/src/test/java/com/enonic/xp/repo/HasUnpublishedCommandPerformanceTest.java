package com.enonic.xp.repo;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import com.enonic.xp.node.Node;
import com.enonic.xp.repo.bootstrap.PerformanceTestBootstrap;
import com.enonic.xp.repo.bootstrap.benchmark.BaseBenchmarkState;
import com.enonic.xp.repo.bootstrap.benchmark.BenchmarkState;
import com.enonic.xp.repo.bootstrap.benchmark.DynamicBenchmarkState;

public class HasUnpublishedCommandPerformanceTest
{
    @Test
    @Disabled
    public void testReferencePerformance()
        throws Exception
    {
        Options opt = new OptionsBuilder().include( this.getClass().getName() + ".*" ).mode( Mode.AverageTime ).timeUnit(
            TimeUnit.SECONDS ).warmupTime( TimeValue.seconds( 1 ) ).warmupIterations( 10 ).measurementTime(
            TimeValue.seconds( 1 ) ).measurementIterations( 10 ).threads( 1 ).forks( 1 ).shouldFailOnError( true ).build();

        new Runner( opt ).run();
    }

    @Benchmark
    public void nonPublished( BenchmarkState state, Blackhole bh )
    {
        bh.consume( PerformanceTestBootstrap.CONTEXT_DRAFT.callWith( () -> run( state, state.NON_PUBLISHED_NODES_ROOT, true ) ) );
    }

    @Benchmark
    public void allPublished( BenchmarkState state, Blackhole bh )
    {
        bh.consume( PerformanceTestBootstrap.CONTEXT_DRAFT.callWith( () -> run( state, state.PUBLISHED_NODES_ROOT, false ) ) );
    }

    @Benchmark
    public void halfPublished( BenchmarkState state, Blackhole bh )
    {
        bh.consume( PerformanceTestBootstrap.CONTEXT_DRAFT.callWith( () -> run( state, state.HALF_PUBLISHED_NODES_ROOT, true ) ) );
    }

    @Benchmark
    @Group("dynamicPublishing")
    public void dynamicPublished( DynamicBenchmarkState state, Blackhole bh )
    {
        bh.consume( PerformanceTestBootstrap.CONTEXT_DRAFT.callWith( () -> run( state, state.PUBLISHED_DYNAMIC_ROOT, true ) ) );
    }

    boolean run( BaseBenchmarkState state, final Node parent, final boolean expectedResult )
    {
        final boolean result = state.prepareHasUnpublishedChildrenCommandBuilder().
            parent( parent.id() ).
            build().
            execute();

        Assertions.assertEquals( expectedResult, result );

        return result;
    }
}
