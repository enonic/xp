package com.enonic.xp.repo.bootstrap.benchmark;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class BenchmarkState
    extends BaseBenchmarkState
{
    @Setup
    public void setup()
    {
        super.setup();
    }

    @TearDown
    public void teardown()
        throws Exception
    {
        super.teardown();
    }
}
