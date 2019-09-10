package com.enonic.xp.script.impl;

import org.junit.jupiter.api.Test;

public class GlobalScopeTest
    extends AbstractScriptTest
{
    @Test
    public void testScope()
        throws Exception
    {
        runTestScript( "global/main.js" );
    }
}
