package com.enonic.xp.lib.task;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

class SleepHandlerTest
    extends ScriptTestSupport
{

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
    }

    @Test
    void testExample()
    {
        runScript( "/lib/xp/examples/task/sleep.js" );
    }

    @Test
    void testSleep200()
    {
        runFunction( "/test/sleep-test.js", "sleep" );
    }

}
