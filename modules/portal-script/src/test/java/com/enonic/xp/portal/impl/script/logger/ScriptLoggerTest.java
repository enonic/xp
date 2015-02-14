package com.enonic.xp.portal.impl.script.logger;

import org.junit.Test;

import com.enonic.xp.portal.impl.script.AbstractScriptTest;

public class ScriptLoggerTest
    extends AbstractScriptTest
{
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
        runTestScript( "logging/format-test.js" );
    }
}
