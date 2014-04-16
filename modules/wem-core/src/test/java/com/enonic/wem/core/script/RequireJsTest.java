package com.enonic.wem.core.script;

import org.junit.Test;

public class RequireJsTest
    extends AbstractJsTest
{
    @Test
    public void testAbsolute()
    {
        execTest( "require-1.0.0:/absolute/test.js" );
    }

    @Test
    // Failes right now. Will try rhino's own require implementation.
    public void testCyclic()
    {
        execTest( "require-1.0.0:/cyclic/test.js" );
    }

    @Test
    public void testRelative()
    {
        execTest( "require-1.0.0:/relative/test.js" );
    }

    @Test
    public void testTransitive()
    {
        execTest( "require-1.0.0:/transitive/test.js" );
    }
}
