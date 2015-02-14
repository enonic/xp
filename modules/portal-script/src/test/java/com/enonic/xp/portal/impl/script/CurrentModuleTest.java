package com.enonic.xp.portal.impl.script;

import org.junit.Test;

public class CurrentModuleTest
    extends AbstractScriptTest
{
    @Test
    public void testCurrentModule()
        throws Exception
    {
        runTestScript( "module-test.js" );
    }
}
