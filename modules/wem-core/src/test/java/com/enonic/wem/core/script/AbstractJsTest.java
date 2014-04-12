package com.enonic.wem.core.script;

import org.junit.Before;

import junit.framework.Assert;

public abstract class AbstractJsTest
{
    @Before
    public final void setup()
    {
    }

    protected final void execTest( final String path )
    {
    }

    public final class TestUtils
    {
        public void assertTrue( final boolean value, final String message )
        {
            Assert.assertTrue( message, value );
        }
    }
}
