package com.enonic.xp.script.impl.function;


import org.junit.Test;

import com.enonic.xp.script.impl.AbstractScriptTest;

public class RequireFunctionTest extends AbstractScriptTest
{
    @Test
    public void testDefaultResourceExecuted()
        throws Exception
    {
        runTestScript( "function/default.js" );//index.js supposed to be run
    }
}
