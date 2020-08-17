package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;

public class ModifyProjectHandlerTest
    extends BaseProjectHandlerTest
{
    @Test
    public void modifyProject()
        throws Exception
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyProject" );
    }

    @Test
    public void modifyProjectDescription()
        throws Exception
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyDescription" );
    }

    @Test
    public void modifyProjectDisplayName()
        throws Exception
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyDisplayName" );
    }

    @Test
    public void modifyProjectLanguage()
        throws Exception
    {
        runFunction( "/test/ModifyProjectHandlerTest.js", "modifyLanguage" );
    }

}
