package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

class CreateProjectHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    void createProject()
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProject" );
    }


    @Test
    void createProjectWithOneParent()
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithOneParent" );
    }

    @Test
    void createProjectWithoutLanguage()
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithoutLanguage" );
    }

    @Test
    void createProjectWithoutPermissions()
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithoutPermissions" );
    }

    @Test
    void createProjectWithoutReadAccess()
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithoutReadAccess" );
    }

    @Test
    void createProjectWithApplications()
    {
        runFunction( "/test/CreateProjectHandlerTest.js", "createProjectWithApplications" );
    }

}
