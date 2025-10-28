package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

class GetAvailableApplicationsHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    void getAvailableApplications()
    {
        runFunction( "/test/GetAvailableApplicationsTest.js", "getAvailableApplications" );
    }

    @Test
    void getProjectWithoutApplications()
    {
        runFunction( "/test/GetAvailableApplicationsTest.js", "getProjectWithoutApplications" );
    }

    @Test
    void getProjectNull()
    {
        runFunction( "/test/GetAvailableApplicationsTest.js", "getProjectNull" );
    }

    @Test
    void getProjectNotExist()
    {
        runFunction( "/test/GetAvailableApplicationsTest.js", "getProjectNotExist" );
    }

}
