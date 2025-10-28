package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

class ModifyProjectPermissionsHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    void addPermissions()
    {
        runFunction( "/test/ModifyProjectPermissionsHandlerTest.js", "addPermissions" );
    }

    @Test
    void addPermissionsNull()
    {
        runFunction( "/test/ModifyProjectPermissionsHandlerTest.js", "addPermissionsNull" );
    }

    @Test
    void removePermissions()
    {
        runFunction( "/test/ModifyProjectPermissionsHandlerTest.js", "removePermissions" );
    }

    @Test
    void removePermissionsNull()
    {
        runFunction( "/test/ModifyProjectPermissionsHandlerTest.js", "removePermissionsNull" );
    }

}
