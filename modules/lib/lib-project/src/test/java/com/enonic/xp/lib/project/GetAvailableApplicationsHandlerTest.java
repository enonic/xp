package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

public class GetAvailableApplicationsHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    public void getAvailableApplications()
        throws Exception
    {
        runFunction( "/test/GetAvailableApplicationsTest.js", "getAvailableApplications" );
    }

    @Test
    public void getProjectWithoutApplications()
        throws Exception
    {
        runFunction( "/test/GetAvailableApplicationsTest.js", "getProjectWithoutApplications" );
    }

    @Test
    public void getProjectNull()
        throws Exception
    {
        runFunction( "/test/GetAvailableApplicationsTest.js", "getProjectNull" );
    }

    @Test
    public void getProjectNotExist()
        throws Exception
    {
        runFunction( "/test/GetAvailableApplicationsTest.js", "getProjectNotExist" );
    }

}
