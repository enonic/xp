package com.enonic.xp.script.impl;

import org.junit.jupiter.api.Test;

class GlobalScopeTest
    extends AbstractScriptTest
{
    @Test
    void testScope()
    {
        runTestScript( "global/main.js" );
    }
}
