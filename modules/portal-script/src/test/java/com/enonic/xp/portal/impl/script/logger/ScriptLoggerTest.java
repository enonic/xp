package com.enonic.xp.portal.impl.script.logger;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.portal.impl.script.AbstractScriptTest;

public class ScriptLoggerTest
    extends AbstractScriptTest
{

    @Before
    public void setUp()
        throws Exception
    {
        mockResource( "mymodule:/logging/log-test.js" );
    }

    @Test
    public void testLog()
        throws Exception
    {
        runTestScript( "logging/log-test.js" );
    }

    @Test
    public void testFormat()
        throws Exception
    {
        runTestScript( "logging/log-test.js" );
    }
}
