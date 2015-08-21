package com.enonic.xp.script.impl.util;

import org.junit.Test;

import com.enonic.xp.script.impl.AbstractScriptTest;

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
