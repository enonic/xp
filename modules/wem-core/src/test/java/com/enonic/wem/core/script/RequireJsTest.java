package com.enonic.wem.core.script;

import org.junit.Test;

public class RequireJsTest
    extends AbstractJsTest
{
    @Test
    public void testAbsolute()
    {
        execTest( "require/absolute/test" );
    }

    @Test
    public void testCyclic()
    {
        execTest( "require/cyclic/test" );
    }

    @Test
    public void testMissing()
    {
        execTest( "require/missing/test" );
    }

    @Test
    public void testRelative()
    {
        execTest( "require/relative/test" );
    }

    @Test
    public void testTransitive()
    {
        execTest( "require/transitive/test" );
    }
}
