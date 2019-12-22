package com.enonic.xp.repo.impl.node;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.version.TestQueryType;

public class ResolveSyncWorkPerformanceTest
{
    @Test
    public void testReferencePerformance()
        throws Exception
    {
        Options opt = new OptionsBuilder().include( this.getClass().getName() + ".*" ).mode( Mode.AverageTime ).timeUnit(
            TimeUnit.SECONDS ).warmupTime( TimeValue.seconds( 1 ) ).warmupIterations( 20 ).measurementTime(
            TimeValue.seconds( 1 ) ).measurementIterations( 10 ).threads( 1 ).forks( 1 ).shouldFailOnError( true ).build();

        new Runner( opt ).run();
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState
        extends ResolveSyncWorkPerformanceBootstrap
    {
        ResolveSyncWorkCommand.Builder command;

        @Setup
        public void setup()
        {
            startClient();
            setupServices();
            command = prepareResolveSyncWorkCommandBuilder();
        }

        @TearDown
        public void teardown()
            throws Exception
        {
            stopClient();
        }
    }

    @Benchmark
    public void inMemory( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith( () -> run( state.command, TestQueryType.IN_MEMORY ) ) );
    }

    @Benchmark
    public void composite( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith( () -> run( state.command, TestQueryType.COMPOSITE ) ) );
    }

    @Benchmark
    public void rare( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith( () -> run( state.command, TestQueryType.RARE ) ) );
    }

    @Benchmark
    public void sortedTerms( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith( () -> run( state.command, TestQueryType.SORTED_TERMS ) ) );
    }

    //@Benchmark
    public void branches( BenchmarkState state, Blackhole bh )
    {
        bh.consume(
            ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith( () -> run( state.command, TestQueryType.BRANCHES_IN_VERSIONS ) ) );
    }

    int run( ResolveSyncWorkCommand.Builder command, TestQueryType type )
    {
        final ResolveSyncWorkResult resolvedNodes = command.testQueryType( type ).build().execute();

        return resolvedNodes.getSize();
    }
}
