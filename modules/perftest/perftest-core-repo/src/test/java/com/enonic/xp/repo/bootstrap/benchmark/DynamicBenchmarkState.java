package com.enonic.xp.repo.bootstrap.benchmark;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Group)
public class DynamicBenchmarkState
    extends BaseBenchmarkState
{
    public int publishCount = 0;

    @Setup
    public void setup()
    {
        super.setup();

        CONTEXT_DRAFT.callWith( () -> {

            this.unpublish( PUBLISHED_DYNAMIC_ROOT, true );
            publish( 0, PUBLISHED_DYNAMIC_ROOT );

            return 1;
        } );

    }

    @Setup(Level.Iteration)
    public void beforeEach()
    {
        this.publish( ++this.publishCount, this.PUBLISHED_DYNAMIC_ROOT );
    }

    @TearDown
    public void teardown()
        throws Exception
    {
        super.teardown();
    }
}
