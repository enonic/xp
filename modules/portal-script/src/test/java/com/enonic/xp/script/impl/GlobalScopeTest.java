package com.enonic.xp.script.impl;

import org.junit.Test;

import com.enonic.xp.script.impl.AbstractScriptTest;

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
