package com.enonic.xp.launcher.impl.weaver;

import java.io.IOException;
import java.util.List;

import com.enonic.xp.trace.Traced;
import com.enonic.xp.trace.Tracer;

/**
 * Fixture woven by {@link TraceWeaverTransformer} in tests. The unwoven version on the test classpath is only
 * used as a source of class bytes and for behavior comparison.
 */
public class TracedFixture
    implements TracedFixtureApi
{
    private int voidCalls;

    @Override
    @Traced("fixture.hello")
    public String hello( final String who )
    {
        Tracer.withCurrent( trace -> trace.attribute( "who", who ) );
        return "Hello " + who;
    }

    @Override
    @Traced
    public long add( final int a, final long b, final double c )
    {
        return a + b + (long) c;
    }

    @Override
    @Traced("fixture.voidWork")
    public void voidWork()
    {
        this.voidCalls++;
    }

    @Override
    public int getVoidCalls()
    {
        return this.voidCalls;
    }

    @Override
    @Traced("fixture.fail")
    public void failWork()
        throws IOException
    {
        throw new IOException( "fixture failed" );
    }

    @Override
    @Traced("fixture.nested")
    public String nested( final String who )
    {
        return hello( who );
    }

    @Override
    public String notTraced()
    {
        return "plain";
    }

    @Override
    @Traced("fixture.generic")
    public List<String> firstAndLast( final List<String> values )
    {
        return List.of( values.get( 0 ), values.get( values.size() - 1 ) );
    }

    @Traced("fixture.static")
    public static long twice( final long value )
    {
        return value * 2;
    }

    @Override
    @Traced("fixture.sync")
    public synchronized long syncTwice( final long value )
    {
        return value * 2;
    }
}
