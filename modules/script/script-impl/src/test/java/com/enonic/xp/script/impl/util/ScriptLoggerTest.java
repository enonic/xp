package com.enonic.xp.script.impl.util;

import org.junit.jupiter.api.Test;

import com.enonic.xp.script.impl.AbstractScriptTest;

class ScriptLoggerTest
    extends AbstractScriptTest
{
    @Test
    void testLog()
    {
        runTestScript( "logging/log-test.js" );
    }

    @Test
    void testFormat()
    {
        runTestScript( "logging/format-test.js" );
    }
}
