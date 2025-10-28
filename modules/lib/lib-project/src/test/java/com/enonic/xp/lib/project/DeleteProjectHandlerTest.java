package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

class DeleteProjectHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    void deleteProject()
    {
        runFunction( "/test/DeleteProjectHandlerTest.js", "deleteProject" );
    }

    @Test
    void deleteNonExistProject()
    {
        runFunction( "/test/DeleteProjectHandlerTest.js", "deleteNotExistProject" );
    }

    @Test
    void deleteProjectNull()
    {
        runFunction( "/test/DeleteProjectHandlerTest.js", "deleteProjectNull" );
    }
}

