package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

public class ListProjectsHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    public void listProjects()
        throws Exception
    {
        runFunction( "/test/ListProjectsHandlerTest.js", "listProjects" );
    }

}
