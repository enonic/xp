package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

public class ModifyProjectReadAccessHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    public void modifyReadAccess()
        throws Exception
    {
        runFunction( "/test/ModifyProjectReadAccessHandlerTest.js", "modifyReadAccess" );
    }

    @Test
    public void modifyReadAccessNull()
        throws Exception
    {
        runFunction( "/test/ModifyProjectReadAccessHandlerTest.js", "modifyReadAccessNull" );
    }


}
