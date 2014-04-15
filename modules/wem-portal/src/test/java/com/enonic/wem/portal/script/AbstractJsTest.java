package com.enonic.wem.portal.script;

import org.junit.Before;

import junit.framework.Assert;

import com.enonic.wem.portal.script.runner.ScriptRunnerImpl;

public abstract class AbstractJsTest
{
    @Before
    public final void setup()
    {
    }

    protected final void execTest( final String path )
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        
        runner.execute();
    }

    public final class TestUtils
    {
        public void assertTrue( final boolean value, final String message )
        {
            Assert.assertTrue( message, value );
        }
    }
}
