package com.enonic.xp.lib.common;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

class SanitizeTest
    extends ScriptTestSupport
{

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
    }

    @Test
    void testExamples()
    {
        runScript( "/lib/xp/examples/common/sanitize.js" );
    }
}
