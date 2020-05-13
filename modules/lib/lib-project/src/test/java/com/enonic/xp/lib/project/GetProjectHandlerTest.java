package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

public class GetProjectHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    public void getProject()
        throws Exception
    {
        runFunction( "/test/GetProjectHandlerTest.js", "getProject" );
    }

    @Test
    public void getProjectWithoutPermissions()
        throws Exception
    {
        runFunction( "/test/GetProjectHandlerTest.js", "getProjectWithoutPermissions" );
    }

    @Test
    public void getProjectNull()
        throws Exception
    {
        runFunction( "/test/GetProjectHandlerTest.js", "getProjectNull" );
    }

    @Test
    public void getProjectNotExist()
        throws Exception
    {
        runFunction( "/test/GetProjectHandlerTest.js", "getProjectNotExist" );
    }

}
