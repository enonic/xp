package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

public class CreateProjectHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    public void createProject()
        throws Exception
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProject" );
    }


    @Test
    public void createProjectWithParent()
        throws Exception
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithParent" );
    }

    @Test
    public void createProjectWithoutLanguage()
        throws Exception
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithoutLanguage" );
    }

    @Test
    public void createProjectWithoutPermissions()
        throws Exception
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithoutPermissions" );
    }

    @Test
    public void createProjectWithoutReadAccess()
        throws Exception
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithoutReadAccess" );
    }

}
