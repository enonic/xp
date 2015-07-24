package com.enonic.xp.portal.impl.script;

import org.junit.Before;
import org.junit.Test;

public class CurrentApplicationTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
        throws Exception
    {
        mockResource( "mymodule:/module-test.js" );
    }

    @Test
    public void testCurrentModule()
        throws Exception
    {
        runTestScript( "module-test.js" );
    }
}
