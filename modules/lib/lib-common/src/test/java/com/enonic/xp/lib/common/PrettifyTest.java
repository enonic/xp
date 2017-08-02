package com.enonic.xp.lib.common;

import org.junit.Test;

import com.enonic.xp.testing.script.ScriptTestSupport;

public class PrettifyTest
    extends ScriptTestSupport
{

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
    }

    @Test
    public void testExamples()
    {
        runScript( "/site/lib/xp/examples/common/prettify.js" );
    }
}
