package com.enonic.xp.portal.impl.script;

import org.junit.Test;

public class CurrentApplicationTest
    extends AbstractScriptTest
{
    @Test
    public void testCurrentApplication()
        throws Exception
    {
        runTestScript( "application-test.js" );
    }
}
