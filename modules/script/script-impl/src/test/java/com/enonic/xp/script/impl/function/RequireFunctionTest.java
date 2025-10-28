package com.enonic.xp.script.impl.function;


import org.junit.jupiter.api.Test;

import com.enonic.xp.script.impl.AbstractScriptTest;

class RequireFunctionTest
    extends AbstractScriptTest
{
    @Test
    void testDefaultResourceExecuted()
    {
        runTestScript( "site/function/default.js" );//index.js supposed to be run
    }
}
