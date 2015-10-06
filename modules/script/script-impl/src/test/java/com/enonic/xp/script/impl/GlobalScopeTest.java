package com.enonic.xp.script.impl;

import org.junit.Test;

public class GlobalScopeTest
    extends AbstractScriptTest
{
    @Test
    public void testScope()
        throws Exception
    {
        runTestScript( "site/global/main.js" );
    }
}
