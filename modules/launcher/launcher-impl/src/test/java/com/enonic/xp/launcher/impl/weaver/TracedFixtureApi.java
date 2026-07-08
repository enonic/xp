package com.enonic.xp.launcher.impl.weaver;

import java.io.IOException;
import java.util.List;

/**
 * Interface implemented by {@link TracedFixture}. Loaded by the test class loader so that woven fixture
 * instances (defined in a child class loader) can be called without reflection.
 */
public interface TracedFixtureApi
{
    String hello( String who );

    long add( int a, long b, double c );

    void voidWork();

    int getVoidCalls();

    void failWork()
        throws IOException;

    String nested( String who );

    String notTraced();

    List<String> firstAndLast( List<String> values );
}
