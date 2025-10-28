package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

class GetProjectHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    void getProject()
    {
        runFunction( "/test/GetProjectHandlerTest.js", "getProject" );
    }

    @Test
    void getProjectWithoutPermissions()
    {
        runFunction( "/test/GetProjectHandlerTest.js", "getProjectWithoutPermissions" );
    }

    @Test
    void getProjectNull()
    {
        runFunction( "/test/GetProjectHandlerTest.js", "getProjectNull" );
    }

    @Test
    void getProjectNotExist()
    {
        runFunction( "/test/GetProjectHandlerTest.js", "getProjectNotExist" );
    }

}
