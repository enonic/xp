package com.enonic.xp.script.impl;

import org.junit.jupiter.api.Test;

class CurrentApplicationTest
    extends AbstractScriptTest
{
    @Test
    void testCurrentApplication()
    {
        runTestScript( "application-test.js" );
    }
}
