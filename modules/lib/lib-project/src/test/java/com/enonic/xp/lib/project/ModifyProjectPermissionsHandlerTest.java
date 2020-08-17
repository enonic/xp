package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

public class ModifyProjectPermissionsHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    public void addPermissions()
        throws Exception
    {
        runFunction( "/test/ModifyProjectPermissionsHandlerTest.js", "addPermissions" );
    }

    @Test
    public void addPermissionsNull()
        throws Exception
    {
        runFunction( "/test/ModifyProjectPermissionsHandlerTest.js", "addPermissionsNull" );
    }

    @Test
    public void removePermissions()
        throws Exception
    {
        runFunction( "/test/ModifyProjectPermissionsHandlerTest.js", "removePermissions" );
    }

    @Test
    public void removePermissionsNull()
        throws Exception
    {
        runFunction( "/test/ModifyProjectPermissionsHandlerTest.js", "removePermissionsNull" );
    }

}
