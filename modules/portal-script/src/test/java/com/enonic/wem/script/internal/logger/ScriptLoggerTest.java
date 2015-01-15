package com.enonic.wem.script.internal.logger;

import org.junit.Test;

import com.enonic.wem.script.AbstractScriptTest;

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
