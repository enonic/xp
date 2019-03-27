package com.enonic.xp.lib.common;

import org.junit.Test;

import com.enonic.xp.testing.ScriptTestSupport;

public class SanitizeTest
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
        runScript( "/lib/xp/examples/common/sanitize.js" );
    }
}
