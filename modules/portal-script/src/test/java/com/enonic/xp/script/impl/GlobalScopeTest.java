package com.enonic.xp.script.impl;

import org.junit.Test;

public class GlobalScopeTest
    extends AbstractScriptTest
{
    @Test
    // @Ignore("Failure since require's are isolated into other scope")
    public void testScope()
        throws Exception
    {
        runTestScript( "global/main.js" );
    }
}
