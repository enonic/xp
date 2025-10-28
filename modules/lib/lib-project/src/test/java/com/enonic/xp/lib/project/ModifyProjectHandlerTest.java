package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

class ModifyProjectHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    void modifyProject()
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyProject" );
    }

    @Test
    void modifyProjectDescription()
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyDescription" );
    }

    @Test
    void modifyProjectDisplayName()
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyDisplayName" );
    }

    @Test
    void modifyProjectLanguage()
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyLanguage" );
    }

    @Test
    void modifyProjectApplications()
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyApplications" );
    }

}
