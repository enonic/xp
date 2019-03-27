package com.enonic.xp.lib.task;

import org.junit.Test;

import com.enonic.xp.testing.ScriptTestSupport;

public class SleepHandlerTest
    extends ScriptTestSupport
{

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
    }

    @Test
    public void testExample()
    {
        runScript( "/lib/xp/examples/task/sleep.js" );
    }

    @Test
    public void testSleep200()
        throws Exception
    {
        runFunction( "/test/sleep-test.js", "sleep" );
    }

}