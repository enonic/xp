package com.enonic.wem.script;

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
