package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

public class DeleteProjectHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    public void deleteProject()
        throws Exception
    {
        runFunction( "/test/DeleteProjectHandlerTest.js", "deleteProject" );
    }

    @Test
    public void deleteNonExistProject()
        throws Exception
    {
        runFunction( "/test/DeleteProjectHandlerTest.js", "deleteNotExistProject" );
    }

    @Test
    public void deleteProjectNull()
        throws Exception
    {
        runFunction( "/test/DeleteProjectHandlerTest.js", "deleteProjectNull" );
    }
}

