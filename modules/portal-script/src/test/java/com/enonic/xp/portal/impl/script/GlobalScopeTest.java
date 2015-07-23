package com.enonic.xp.portal.impl.script;

import org.junit.Before;
import org.junit.Test;

public class GlobalScopeTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
        throws Exception
    {
        mockResource( "mymodule:/global/main.js" );
        mockResource( "mymodule:/global/library.js" );
    }

    @Test
    // @Ignore("Failure since require's are isolated into other scope")
    public void testScope()
        throws Exception
    {
        runTestScript( "global/main.js" );
    }
}
